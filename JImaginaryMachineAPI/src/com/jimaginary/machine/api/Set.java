/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import org.openide.windows.InputOutput;

/**
 *
 * @author simonkenny
 */
public class Set {
    public final static int DISPLAY_VALUES = 0;
    public final static int DISPLAY_GRAPH = 1;

    public int GRAPH_WIDTH = 80;
    public int GRAPH_HEIGHT = 8;

    private int SUBSET_COUNT = 0;

    private final boolean OUT_OF_BOUNDS_WRITE_PROTECT_DEFAULT_VALUE = false;

    public final static int WRITE_MODE_REPLACE = 0;
    public final static int WRITE_MODE_ADD = 1;
    // TODO : add another mode with auto compression

    int writeHead, lastWriteHead, readHead, lastReadHead;
    int len;
    float []values;
    // switch on or off auto stop when writehead goes over end
    public boolean outOfBoundsWriteProtect;
    public boolean canWrite;
    boolean writeProtectSwitch;
    protected int writeMode;

    private final int displayMode;

    // range
    int rangeBlockSize;
    
    String name;
    String description;

    public Set( String _name, int _len, int _rangeBlockSize, int _displayMode ) {
        name = _name;
        len = _len;
        rangeBlockSize = _rangeBlockSize;
        displayMode = _displayMode;
        values = new float[len];
        writeHead = len - 1; // NOTE!!! this is so when the sequence goes to the next note, it will be the first space
        lastWriteHead = 0;
        readHead = len - 1;
        lastReadHead = 0;
        // write protection
        outOfBoundsWriteProtect = OUT_OF_BOUNDS_WRITE_PROTECT_DEFAULT_VALUE;
        canWrite = true;
        writeProtectSwitch = false;
        writeMode = WRITE_MODE_REPLACE;
    }

    protected void setWriteMode( int mode ) {
        writeMode = mode;
    }
    
    public int getLen() {
        return len;
    }
    
    public float[] getValues() {
        return values;
    }
    
    public String getName() {
        return name;
    }


    // ---- WRITING ----

    public boolean writeSet( Set set ) {
        System.out.println(name+"\t\t - writeSet");
        if( !canWrite || set == null ) {
            System.out.println(name+"\t\t - can't write set");
            return false;
        }
        if( outOfBoundsWriteProtect && writeHead < (len*0.1) && lastWriteHead > (len*0.9) ) {
            // compensate for the first time we write to this sequence, the first value of writeHead will be len-1 and
            // so will trigger the outOfBoundsWriteProtect, as lastWriteHead = len-1 > writeHead = 0
            // this switch ensures the wrap around is allowed once, and then not the next time
            if( !writeProtectSwitch ) {
                writeProtectSwitch = true;
                System.out.println( name+"\t\t- writeProtectSwitch ON");
            } else {
                System.out.println( name+"\t\t- stopped writing to idx "+writeHead);
                canWrite = false;
                writeHead = len - 1;
                return false;
            }
        }
        // otherwise, write
        // but first make sure write head is in bounds
        writeHead = writeHead % len;
        while(writeHead < 0) {
            writeHead = (writeHead+len) % len;
        }
        // then write
        //int lastReadHead_otherSet = set.readHead;
        int num = set.len;
        //System.out.println(name+"\t\t - writing "+num+" elements from "+set.name);
        for( int i = 0 ; i < num ; i++ ) {
            if( i > 0 ) {
                writeHead = (++writeHead) % len;
            }
            if( writeMode == WRITE_MODE_REPLACE ) {
                values[writeHead] = set.readValueAndInc();
            } else if( writeMode == WRITE_MODE_ADD ) {
                values[writeHead] += set.readValueAndInc();
            }
            //System.out.println(name+"\t\t - i:"+i+" ; ["+writeHead+"] = "+values[writeHead]);
        }
        //set.lastReadHead = lastReadHead_otherSet;
        //display();
        return true;
    }

    public boolean writeValue( float val ) {
        return writeValue(val,writeHead);
    }

    public boolean writeValue( float val, int idx ) {
        if( !canWrite ) {
            return false;
        }
        if( outOfBoundsWriteProtect && lastWriteHead > writeHead ) {
            // compensate for the first time we write to this sequence, the first value of writeHead will be len-1 and
            // so will trigger the outOfBoundsWriteProtect, as lastWriteHead = len-1 > writeHead = 0
            // this switch ensures the wrap around is allowed once, and then not the next time
            if( !writeProtectSwitch ) {
                writeProtectSwitch = true;
                System.out.println( name+"\t\t- writeProtectSwitch ON");
            } else {
                System.out.println( name+"\t\t- stopped writing to idx "+idx);
                canWrite = false;
                writeHead = len - 1;
                return false;
            }
        }
        writeHead = idx % len;
        while( writeHead < 0 ) {
            writeHead += len;
        }
        values[writeHead] = val;
        //println( "Sequence:write "+val +" to idx:"+writeHead+" (orig "+idx+")");
        // update gfx
        //setValueText();
        return true;
    }

    public boolean writeValueAndInc( float val ) {
        return writeValueAndInc( val, writeHead );
    }

    public boolean writeValueAndInc( float val, int idx ) {
        boolean ret = writeValue( val, idx );
        lastWriteHead = writeHead;
        writeHead = (++writeHead) % len;
        return ret;
    }

    public void writeHeadTo(int idx) {
        lastWriteHead = writeHead;
        writeHead = idx % len;
        while( writeHead < 0 ) {
            writeHead += len;
        }
    }

    public void writeHeadMoveToRnd() {
        lastWriteHead = writeHead;
        writeHead = (int)(Math.random()*(len-1));
        System.out.println( name+"\t\t- write head to random: "+writeHead);
    }	

    public void writeHeadMoveByOffset( int offset ) {
        lastWriteHead = writeHead;
        writeHead = (writeHead + offset) % len;
        System.out.println( name+"\t\t- write head from "+lastWriteHead+" to "+writeHead);
    }

    // note should be mod 12 (this is done here also for safety but should be expected)
    // - for equidistant values, the first one encoutered (with lower idx) is closest
    public void writeHeadToClosestMatch( float val ) {
        int closest = 0;
        float dist = 10000;
        for( int i = 0 ; i < values.length ; i++ ) {
            if( Math.abs(values[i] - val) < dist ) {
                dist = Math.abs(values[i] - val);
                closest = i;
            }
        }
        lastWriteHead = writeHead;
        writeHead = closest;
    }

    public void writeHeadToClosestMatch( float val, int direction ) {
        int closest = 0;
        float dist = 10000;
        if( direction < 0 ) {
            for( int i = 0 ; i < len ; i++ ) {
                if( Math.abs(values[i] - val) < dist && values[i] <= val ) {
                    dist = Math.abs(values[i] - val);
                    closest = i;
                }
            }
        } else {
            for( int i = 0; i < len ; i++ ) {
                if( Math.abs(values[i] - val) < dist && values[i] >= val ) {
                    dist = Math.abs(values[i] - val);
                    closest = i;
                }
            }
        }
        lastWriteHead = writeHead;
        writeHead = closest;
    }


    // ---- READING ----
    public float readLastValue() {
        return values[lastReadHead];
    }

    public float readValue() {
        return values[readHead];
    }

    public float readValue( int idx ) {
        readHead = idx % len;
        //println( name+"\t\t- read "+values[readHead]+" from to idx:"+readHead+" (orig "+idx+")");
        return values[readHead];
    }

    public float readValueAndInc() {
        return readValueAndInc(readHead);
    }

    public float readValueAndInc( int idx ) {
        float ret = readValue(idx);
        readHead = (++readHead) % len;
        return ret;
    }

    public Set subSet( int numElements ) {
        System.out.println(name+":subSet called, size "+numElements);
        Set newSet = new Set("SubSet-"+(SUBSET_COUNT++),numElements,rangeBlockSize,displayMode);
        newSet.writeHeadMoveByOffset(1);
        for( int i = 0 ; i < numElements ; i++ ) {
                if( i > 0 ) {
                        readHead = (readHead+1) % len;
                }
                newSet.writeValueAndInc(readValue());
        }
        return newSet;
    }

    // --- Read head movement ---

    // returns true if wrapped around
    public boolean readHeadInc() {
        lastReadHead = readHead;
        readHead = (readHead+1) % len;
        return readHead < lastReadHead;
    }

    public void readHeadTo( int idx ) {
        lastReadHead = readHead;
        readHead = idx % len;
    }

    public void readHeadMoveToRnd() {
        lastReadHead = readHead;
        readHead = (int)(Math.random()*(len-1));
        System.out.println( name+"\t\t- read head to random: "+readHead);
    }

    public void readHeadMoveToRnd( float start, float range ) {
        lastReadHead = readHead;
        readHead = (int)(start + (Math.random()*(range*rangeBlockSize))) % len;
        while( readHead < 0 ) {
            readHead += len;
        }
        System.out.println( name+"\t\t- random in range (s:"+start+",r:"+(range*rangeBlockSize)+"): "+readHead);
    }

    public void readHeadConstrainToRange( float start, float range ) {
        // translate writehead to same position in "blocks" of size length with offset start
        int idx = (int)(readHead-(start*rangeBlockSize)) % (int)(range*rangeBlockSize);
        // correct number to be positive modulo length
        while( idx < 0 ) {
            idx += (int)(range*rangeBlockSize);
        }
        lastReadHead = readHead;
        readHead = (int)(idx + (start*rangeBlockSize)) % len;
        System.out.println( name+"\t\t - readHeadToRange (s:"+start+", r:"+range+") - from "+lastReadHead+" to "+readHead);
    }

    // see notes for readHead
    public void readHeadToClosestMatch( float val ) {
        int closest = 0;
        float dist = 10000;
        for( int i = 0 ; i < values.length ; i++ ) {
            if( Math.abs(values[i] - val) < dist ) {
                dist = Math.abs(values[i] - val);
                closest = i;
            }
        }
        lastReadHead = readHead;
        readHead = closest;
    }

    public void readHeadToClosestMatch( float val, int direction ) {
        int closest = 0;
        float dist = 10000;
        if( direction < 0 ) {
            for( int i = 0 ; i < len ; i++ ) {
                if( Math.abs(values[i] - val) < dist && values[i] <= val ) {
                    dist = Math.abs(values[i] - val);
                    closest = i;
                }
            }
        } else {
            for( int i = 0; i < len ; i++ ) {
                if( Math.abs(values[i] - val) < dist && values[i] >= val ) {
                    dist = Math.abs(values[i] - val);
                    closest = i;
                }
            }
        }
        lastReadHead = readHead;
        readHead = closest;
    }

    public void readHeadMoveByOffset( int offset ) {
        lastReadHead = readHead;
        readHead = (readHead + offset) % len;
        while( readHead < 0 ) {
            readHead += len;
        }
        //System.out.println( name+"\t\t - readHeadMoveByOffset : by "+offset+", from "+lastReadHead+" to "+readHead);
    }


    // --- extra ----

    public void applyFades( int numSamples ) {
        if( len < (numSamples*2) ) {
            return;
        }
        for( int i = 0 ; i < numSamples ; i++ ) {
            // fade in
            values[i] *= (float)i/(float)numSamples;
            // fade out
            values[len-1-i] *= (float)i/(float)numSamples;
        }
    }


    // --- GFX ---

    private void setValueText() {
        String str = "";
        for( int i = 0 ; i < values.length ; i++ ) {
                str += String.format("%.0f",values[i]);
                if( i != (values.length-1) ) {
                        str += ((i+1) % 4 == 0 ? "  ||  " : " | ");
                }
        }
        description = str;
    }

    public void display() {
        if( displayMode == DISPLAY_VALUES ) {
            setValueText();
            ConsoleWindowOut.getInstance().println( name+" contents:   "+description);
        } else if( displayMode == DISPLAY_GRAPH ) {
            ConsoleWindowOut.getInstance().println(name+": displaying");
            String printChar = "0";
            float maxPveVal[] = new float[GRAPH_WIDTH];
            float maxNveVal[] = new float[GRAPH_WIDTH];
            int xBin = (int)((float)values.length/(float)GRAPH_WIDTH);
            for( int x = 0 ; x < GRAPH_WIDTH ; x++ ) {
                for( int i = 0 ; i < xBin ; i++ ) {
                    float val = values[(x*xBin)+i];
                    if( val < 0.f ) {
                        if( val < maxNveVal[x] ) {
                            maxNveVal[x] = val;
                        }
                    } else {
                        if( val > maxPveVal[x] ) {
                            maxPveVal[x] = val;
                        }
                    }
                }
            }
            ConsoleWindowOut.getInstance().println("");
            float yBin = 1.f/(float)GRAPH_HEIGHT;
            for( int y = GRAPH_HEIGHT ; y > 0 ; y-- ) {
                for( int x = 0 ; x < GRAPH_WIDTH ; x++ ) {
                    //print(String.format("%.2f",maxPveVal[x])+", ");
                    if( maxPveVal[x] > (y*yBin)  ) {
                        ConsoleWindowOut.getInstance().print(printChar);
                    } else {
                        ConsoleWindowOut.getInstance().print(" ");
                    }
                }
                ConsoleWindowOut.getInstance().println("");
            }
            for( int x = 0 ; x < GRAPH_WIDTH ; x++ ) {
                ConsoleWindowOut.getInstance().print("-");
            }
            ConsoleWindowOut.getInstance().println("");
            for( int y = 1 ; y <= GRAPH_HEIGHT ; y++ ) {
                for( int x = 0 ; x < GRAPH_WIDTH ; x++ ) {
                    //print(String.format("%.2f",maxNveVal[x])+", ");
                    if( maxNveVal[x] < -(y*yBin) ) {
                        ConsoleWindowOut.getInstance().print(printChar);
                    } else {
                        ConsoleWindowOut.getInstance().print(" ");
                    }
                }
                ConsoleWindowOut.getInstance().println("");
            }
            ConsoleWindowOut.getInstance().println("\n[Graph end]");
        }
    }
}
