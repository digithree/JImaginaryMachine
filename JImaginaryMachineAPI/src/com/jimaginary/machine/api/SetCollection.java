/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.util.ArrayList;

/**
 *
 * @author simonkenny
 */
public class SetCollection {
    public final static int PLAY = 0;
    public final static int PLAY_LOOPING = 1;
    public final static int PAUSE = 2;
    public final static int STOP = 3;

    public final static int SETS_FOLLOW_MODE_POSITION = 0;
    public final static int SETS_FOLLOW_MODE_VALUE_MATCH = 1;

    protected String name;

    protected ArrayList<Set> sets;
    protected Set currentSet;
    protected boolean setsFollowCurrentHead;
    protected int setsFollowMode;

    protected boolean playAllowed;

    protected float rangeStart, rangeLength;
    protected boolean rangeAll;

    public SetCollection() {
        name = "SetCollection";
        sets = new ArrayList<Set>();
        currentSet = null;
        playAllowed = false;
        rangeStart = 0;
        rangeLength = 0;
        rangeAll = true;
        setsFollowCurrentHead = false;
        setsFollowMode = SETS_FOLLOW_MODE_POSITION;
    }

    public void init() {
        // custom stuff for reset
    }

    // --- SET ACCESS ---
    // - IO

    protected void addSet( Set set ) {
        sets.add(set);
    }

    public void inputSet( String file ) {
        System.out.println(name+":inputSet called - not defined for this collection");
        // DO INPUT FROM FILE
    }

    public void outputSet( int idx, String filename ) {
        int useIdx = idx % sets.size();
        while(useIdx < 0) {
            useIdx += sets.size();
        }
        System.out.println(name+":outputSet called - not defined for this collection");
        // DO OUTPUT TO FILE
        // note: if idx == -1, choose all ; if idx >= 0 && < numSets, choose current set only ; otherwise fail
    }

    public void playback( int flag, int idx ) {
        System.out.println(name+":playSet called - not defined for this collection");
        // PLAY
        // note: if idx == -1, choose all ; if idx >= 0 && < numSets, choose current set only ; otherwise fail
    }

    // - DIRECT ACCESS

    public void chooseSet( int idx ) {
        int useIdx = idx % sets.size();
        while(useIdx < 0) {
            useIdx += sets.size();
        }
        currentSet = sets.get(useIdx);
    }

    public boolean chooseSetByOffset( int offset ) {
        if( sets.isEmpty() ) {
            return false;
        }
        System.out.println("\tcurrent set name: "+currentSet.name);
        System.out.println("\tnum sets: "+sets.size());
        int idx = sets.indexOf(currentSet);
        if( idx >= 0 && idx < sets.size() ) {
            System.out.println("\tchooseSetByOffset: current = "+idx);
            idx = (idx + offset) % sets.size();
            System.out.println("\tchooseSetByOffset: new = "+idx);
            currentSet = sets.get(idx);
            return true;
        }
        return false;
    }

    protected int getNumSets() {
        return sets.size();
    }

    protected Set getCurrentSet() {
        return currentSet;
    }

    // - INDIRECT ACCESS

    protected void clearSets() {
        sets.clear();
    }

    protected void clearSetsAndReinitialise() {
        sets.clear();
        init();
    }

    public Set sampleSubSet( boolean rnd, int offset, int size ) {
        System.out.println(name+":sampleSubSet");
        if( currentSet != null ) {
            // Move read head
            if( rnd ) {
                currentSet.readHeadMoveToRnd();
            } else {
                currentSet.readHeadMoveByOffset(offset);
            }
            if( !rangeAll ) {
                currentSet.readHeadConstrainToRange(rangeStart,rangeLength);
            }
            // move other sets to match this sets read head if flag true
            if( setsFollowCurrentHead ) {
                for( Set set : sets ) {
                    if( set != currentSet ) {
                        if( setsFollowMode == SETS_FOLLOW_MODE_POSITION ) {
                            if( rnd ) {
                                set.readHeadMoveToRnd();
                            } else {
                                set.readHeadMoveByOffset(offset);
                            }
                            if( !rangeAll ) {
                                set.readHeadConstrainToRange(rangeStart,rangeLength);
                            }
                        } else if( setsFollowMode == SETS_FOLLOW_MODE_VALUE_MATCH ) {
                            set.readHeadToClosestMatch(currentSet.readValue(),offset);
                        }
                    }
                }
            }
            // sample
            Set returnSet = currentSet.subSet(size);
            System.out.println("Sampled sub set...");
            returnSet.display();		//default, doesn't work for PCM sets
            return returnSet;
        }
        System.out.println(name+":can't sample, currentSet is null");
        return null;
    }

    public boolean writeToSet( boolean rnd, int offset, Set _set ) {
        System.out.println(name+":writeToSet");
        if( currentSet != null ) {
            // Move write head
            if( rnd ) {
                currentSet.writeHeadMoveToRnd();
            } else {
                currentSet.writeHeadMoveByOffset(offset);
            }
            // write
            boolean retFlag = currentSet.writeSet(_set);
            float matchVal = currentSet.readLastValue();
            // move other sets
            if( setsFollowCurrentHead ) {
                for( Set set : sets ) {
                    if( set != currentSet ) {
                        if( setsFollowMode == SETS_FOLLOW_MODE_POSITION ) {
                            if( rnd ) {
                                set.writeHeadMoveToRnd();
                            } else {
                                set.writeHeadMoveByOffset(offset);
                            }
                        } else if( setsFollowMode == SETS_FOLLOW_MODE_VALUE_MATCH ) {
                            set.writeHeadToClosestMatch(matchVal,offset);
                        }
                    }
                }
            }
            // if couldn't write, try moving to other set
            if( !retFlag ) {
                System.out.println(name+": couldn't write to set!");
                for( Set set : sets ) {
                    if( set != currentSet ) {
                        if( set.canWrite ) {
                            retFlag = set.writeSet(_set);
                        }
                    }
                    if( retFlag ) {
                        System.out.println(name+": changed set automatically from "+currentSet.name+" to "+set.name);
                        currentSet = set;
                        break;
                    }
                }
            }
            return retFlag;
        }
        System.out.println(name+": can't write, currentSet is null");
        return false;
    }

    public void display() {
        for( Set set : sets ) {
            set.display();
        }
    }

    // --- MODIFIERS ---

    public void setRange( boolean all, float start, float len ) {
        rangeAll = all;
        rangeStart = start;
        rangeLength = len;
    }

    // --- graph building ---

    public GraphNode generateSampleNode() {
        return null;
    }

    public GraphNode generateWriteNode() {
        return null;
    }

    public GraphNode[] generateManditoryFirstModifyNodes() {
        return null;
    }

    public GraphNode generateModifyNode() {
        return null;
    }
    
    public GraphNode[] generateAllNodes() {
        return null;
    }
}
