/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.digithree.codecs.midi;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import com.jimaginary.machine.api.*;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author simonkenny
 */
public class MidiCodec {
    public static final String[] NOTE_NAMES = { "C_", "C#", "D_", "D#", "E_", "F_", "F#", "G_", "G#", "A_", "A#", "B_" };
    private File midiFile;
    private Sequence sequence;
    private Track[] tracks;
    public int numTracks = 0;

    private long tempo;  // in microseconds per quarter not
    private int tempoBPM;   // in standard interger rounded BPM
    private int timeSignatureNominator = 0;
    private int timeSignatureDenominator = 0;
    public float quarterNotesPerBar = 0;
    private int ticksPerMetronomeClick = 0;
    private int thirtySecondBeatsPerQuarterNote = 0;
    public int ticksPerQuarterNote = 0;

    public double ticksPerSecond = 0;
    public double totalTicks = 0;

    public double framesPerSecond = 0;
    public double ticksPerFrame = 0;
    public double totalFrames = 0;
    public double frameLengthInMicroseconds = 0;

    public int lowestNote = 127;
    public int highestNote = 0;
    public int lowestNoteForTrack[];
    public int highestNoteForTrack[];
    
    public class MidiCodecInfo {
	public ArrayList trackNumber = null;
	public ArrayList message = null;
	public ArrayList value1 = null;
	public ArrayList value2 = null;
        public ArrayList tick = null;

        public double firstTick = 0;
        public double numTicks = 0;
        public float numBars = 0;
        public float numSecs = 0.f;

        public MidiCodecInfo() {
            trackNumber = new ArrayList<Integer>();
            message = new ArrayList<String>();
            value1 = new ArrayList<Integer>();
            value2 = new ArrayList<Integer>();
            tick = new ArrayList<Integer>();
        }
    }


    public MidiCodec() {
      // constructor do stuff? no
    }

    public void displaySetAsString( Set set ) {
      System.out.print( set.getName()+":   " );
      for( int i = 0 ; i < set.getValues().length ; i++ ) {
        if( (int)set.getValues()[i] == 0 ) {
          System.out.print( "...");
        } else {
          System.out.print( noteValToString((int)set.getValues()[i]) );
        }
        if( i != (set.getValues().length-1) ) {
          System.out.print( ((i+1) % 4 == 0 ? "  ||  " : " | ") );
        }
      } 
      System.out.println("");
    }

    public void writeSequenceToFile( Sequence seq, File file ) {
      try {
        int numBytes = MidiSystem.write(seq,1,file);
        System.out.println( "Wrote "+numBytes+" bytes to "+file.getAbsolutePath());
      } catch( IOException e ) {
        System.out.println( "Error writing midi sequence to file: " + e.toString() );
      }
    }

    public Set toSet( int trackNum, int startBar, int endBar ) {
      // get notes in range
      MidiCodecInfo midiCodecInfo = fillMidiMessageBuffer_bars( startBar, endBar );
      // calculate how many eighth notes and how long an eighth note is in ticks
      int divisions = (int)quarterNotesPerBar;
      if( quarterNotesPerBar != (float)divisions ) {
        divisions = (int)(quarterNotesPerBar * 2);
      }
      int numEighthNotes = (int)(divisions * (endBar-startBar+1) * 2);

      Set set = new Set("MIDI-track-"+trackNum,numEighthNotes,divisions,Set.DISPLAY_VALUES);

      int ticksPerEighthNote = ticksPerQuarterNote / 2;
      // extract notes
      int tickNoteStart = 0;
      int noteVelocity = 0;
      int noteVal = 0;
      boolean noteOn = false;
      for( int i = 0 ; i < midiCodecInfo.trackNumber.size() ; i++ ) {
          int thisTrack = (((Integer)midiCodecInfo.trackNumber.get(i)).intValue());
          //if( curTrack < song.numTracks ) {
          // now go through midi messages to see if there are any notes
          if( thisTrack == trackNum ) {
              if( midiCodecInfo.message.get(i).equals( "Note On" )
                  && (((Integer)midiCodecInfo.value2.get(i)).intValue()) != 0 ) {
                if( !noteOn ) {
                    noteOn = true;
                    tickNoteStart = (((Integer)midiCodecInfo.tick.get(i)).intValue());
                    noteVal = (((Integer)midiCodecInfo.value1.get(i)).intValue());
                    noteVelocity = (((Integer)midiCodecInfo.value2.get(i)).intValue());
                    //System.out.System.out.println( "Track "+curTrack+": Note On - tick("+tickNoteStart[curTrack]+"), noteVal("+noteVal[curTrack]+") noteVel("+noteVelocity[curTrack]+")");
                }
              }
              if( midiCodecInfo.message.get(i).equals( "Note Off" )
                    || (noteOn && midiCodecInfo.message.get(i).equals( "Note On" )
                    && (((Integer)midiCodecInfo.value2.get(i)).intValue()) == 0 ) ) {
                if( noteOn ) {
                    noteOn = false;
                    double startTick = (double)(tickNoteStart - midiCodecInfo.firstTick);
                    double endTick = (double)(((Integer)midiCodecInfo.tick.get(i)).intValue()) - midiCodecInfo.firstTick;
                    double tickLen = endTick - startTick;
                    float vel = ((float)noteVelocity) / 127.f;

                    int idx = (int)(startTick / (double)ticksPerEighthNote);
                    //System.out.System.out.println( "noteArray["+(curTrack-1)+"]["+idx+"] = "+(int)noteVal[curTrack]);
                    set.writeHeadTo(idx);
                    set.writeValue( noteVal );
                    // TODO : take into account length of note
                }
              }
          }
      }
      return set;
    }


    // note, Processing can't catch InvalidMidiDataException or IOException exceptions!
    public void openMidiFile( String fileName ) {  
      //String fileName = "testMidi.mid";

      midiFile = new File( fileName );
      System.out.println( "Sequence file name: " + fileName );

      // Using MidiSystem, convert the file to a sequence.
      try {
        sequence = MidiSystem.getSequence( midiFile );
      } 
      catch( InvalidMidiDataException e ) {
        sequence = null;
      } 
      catch( IOException e ) {
        sequence = null;
      }
      if (sequence != null) {
        // Print sequence information
        System.out.println( " length: " + sequence.getTickLength() + " ticks" );
        System.out.println( " duration: " + sequence.getMicrosecondLength() + " micro seconds" );
        System.out.println( " division type: " + divisionTypeToString( sequence.getDivisionType(), sequence.getResolution() ) );
        // No need to store this data in local variables, already
        //      encaspulated nicely in standard classes
        // However we do need the tempo and other info from track 0...
        //       as well as ticksPerSecond
        ticksPerSecond = sequence.getTickLength() / (sequence.getMicrosecondLength()/1000000);
        System.out.println( " ticks per second: " + ticksPerSecond );
      }
      totalTicks = sequence.getTickLength();
      tracks = sequence.getTracks();
      numTracks = tracks.length;

      lowestNoteForTrack = new int[numTracks];
      highestNoteForTrack = new int[numTracks];

      System.out.append( "Number of tracks: " + numTracks + "\n" );
      if ( tracks != null ) {
        for ( int j = 0 ; j < tracks.length ; j++ ) {
          System.out.println( "Track " + j + " info" );
          lowestNoteForTrack[j] = 127;
          highestNoteForTrack[j] = 0;
          Track track = tracks[j];
          for ( int i = 0 ; i < track.size() ; i++ ) {
            MidiEvent event = track.get( i );
            String outStr = "Tick " + event.getTick() + " : ";

            MidiMessage message = event.getMessage();
            if ( message instanceof MetaMessage ) {
              MetaMessage mm = (MetaMessage) message;
              byte[] byteArray = mm.getData();

              switch( mm.getType() ) {
              case 0:     // sequence number
                outStr += "MM Sequence number : " + (((int)byteArray[0] << 8) + (int)byteArray[1]);
                break;
              case 1:     // Text
                outStr += "MM Text : " + new String(byteArray);
                break;
              case 2:     // Copyright notice
                outStr += "MM Copyright notice : " + new String(byteArray);
                break;
              case 3:     // Track name
                outStr += "MM Track name : " + new String(byteArray);
                break;
              case 4:     // Instrument name
                outStr += "MM Instrument : " + new String(byteArray);
                break;
              case 5:     // Lyrics
                outStr += "MM Lyrics : " + new String(byteArray);
                break;
              case 6:     // Marker
                outStr += "MM Marker : " + new String(byteArray);
                break;
              case 7:     // Cue point
                outStr += "MM Cue point : " + new String(byteArray);
                break;
              case 32:    // Channel prefix
                outStr += "MM Channel prefix : " + (int)byteArray[0];
                break;
              case 47:     // End of track
                outStr += "MM End of track";
                break;
              case 81:    // Set tempo
                outStr += "MM Set tempo : ";
                tempo = ((long)byteArray[0] << 16) + ((long)byteArray[1] << 8) + (long)byteArray[2];
                tempoBPM = (int)((double)60.0/((double)tempo/1000000.0));
                outStr += tempo + " (or " + tempoBPM + " bpm)";
                break;
              case 84:    // SMPTE offset
                outStr += "MM SMPTE (data ignored)";
                break;
              case 88:    // Set time signature
                // 4 byte data
                // 1st byte = Tempo numerator
                // 2nd byte = Temp donominator in terms of powers of 2, i.e. denominator = 2^(2nd byte)
                // 3rd byte = Metronome (i.e. just heard metro) number of ticks per click
                // 4th byte = Number of 32nd notes per beat (whole note). Usually 8
                outStr += "MM Set time signature : ";
                timeSignatureNominator = (int)byteArray[0];
                timeSignatureDenominator = (int)Math.pow(2, (double)byteArray[1]);
                quarterNotesPerBar = (float)timeSignatureNominator * (4.f/(float)timeSignatureDenominator);
                ticksPerMetronomeClick = (int)byteArray[2];
                thirtySecondBeatsPerQuarterNote = (int)byteArray[3];
                outStr += timeSignatureNominator + "/" + timeSignatureDenominator + ", " +
                  ticksPerMetronomeClick + " ticks per click, " + thirtySecondBeatsPerQuarterNote + " 32nd notes per beat, "+quarterNotesPerBar+" quarter notes per bar";
                break;
              case 89:    // Key signature
                outStr += "MM Key signature : ";
                if ( (int)byteArray[0] == 0 )
                  outStr += "No sharps/flats - ";
                else if ( (int)byteArray[0] < 0 )
                  outStr += (int)byteArray[0] + "flats - ";
                else if ( (int)byteArray[0] > 0 )
                  outStr += (int)byteArray[0] + "sharps - ";

                if ( (int)byteArray[1] == 0 )
                  outStr += "major key";
                else if ( (int)byteArray[1] == 1 )
                  outStr += "minor key";
                break;
              case 127:
                outStr += "MM Sequencer specific (data ignored)";
                break;
              }
              System.out.println(outStr);
            }
            else if ( message instanceof ShortMessage ) {
              if ( ((ShortMessage)message).getCommand() == ShortMessage.NOTE_ON ) {
                int noteVal = (((Integer)((ShortMessage)message).getData1()).intValue());
                // recheck all tracks lowest/highest note
                if ( noteVal < lowestNote )
                  lowestNote = noteVal;
                if ( noteVal > highestNote )
                  highestNote = noteVal;
                // recheck this track lowest/highest note
                if ( noteVal < lowestNoteForTrack[j] )
                  lowestNoteForTrack[j] = noteVal;
                if ( noteVal > highestNoteForTrack[j] )
                  highestNoteForTrack[j] = noteVal;
              }
            }
          }
          if( lowestNoteForTrack[j] > highestNoteForTrack[j] ) {
            lowestNoteForTrack[j] = 0;
            highestNoteForTrack[j] = 127;
          }
          System.out.println( "Note range of played notes for track " + j + " is " + noteValToString(lowestNoteForTrack[j]) +
                              "(" + lowestNoteForTrack[j] + ") to " + noteValToString(highestNoteForTrack[j]) + "(" +
                              highestNoteForTrack[j] + ")" );
        }
        System.out.println( "Note range of played notes is " + noteValToString(lowestNote) + "(" + lowestNote + ") to " +
          noteValToString(highestNote) + "(" + highestNote + ")" );
      }
    }

    public void calculateFrameSize( double fps ) {
      framesPerSecond = fps;
      System.out.println( "Frames per second: " + framesPerSecond );

      // So how long is a frame in micro seconds?
      frameLengthInMicroseconds = 1000000/(framesPerSecond);
      System.out.println("Frame length in microseconds: " + frameLengthInMicroseconds );

      // And how many ticks per frame? tempo holds numbers of microseconds per 4th note
      // ticksPerFrame = quarterNotesPerFrame * ticksPerQuarterNote
      ticksPerFrame = (frameLengthInMicroseconds/tempo) * sequence.getResolution();
      System.out.println("Ticks per frame: " + ticksPerFrame );

      // Finally, how many frames does that make?
      // Two ways to figure this out:
      // 1) Using tick length (this will rely more on our calcs...
      totalFrames = sequence.getTickLength() / ticksPerFrame;
      System.out.append( "Total number of frames (using tick calc): " + totalFrames + "\n" );
      System.out.append( "Which makes the song " + (totalFrames*framesPerSecond) + " seconds long" + "\n" );
      // 2) Using microsecond length...
      double totalFramesSec = sequence.getMicrosecondLength() / frameLengthInMicroseconds;
      System.out.append( "Total number of frames (using microsec calc): " + totalFramesSec + "\n" );
      System.out.append( "Which makes the song " + (totalFramesSec*framesPerSecond) + " seconds long" + "\n" );
      // Comparison        
      System.out.append( "Difference? " + Math.abs(totalFrames-totalFramesSec) + " frames error margin" + "\n" );

      // Let's stick with the tick version though
      totalFrames = sequence.getTickLength() / ticksPerFrame;
    }

    public void processFrame( int frameNumber ) {
      // Get tick range - note, rounding off happens LAST so all ticks stored
      // in double vars. Just cast as int any time it's needed except when
      // calculating the tick value for the end of the range
      double firstTick = ticksPerFrame * frameNumber;
      //System.out.System.out.println("Processing frame " + frameNumber + ", tick range " + (int)firstTick + " to " + (int)(firstTick+ticksPerFrame) );

      // Get messages for this tick range for each track
      //System.out.System.out.println("\n\nProcessing frame " + frameNumber );
      ArrayList trackMessages;
      for ( int i = 0 ; i < numTracks ; i++ ) {
        // Get messages
        trackMessages = getTrackMessages( i, (int)firstTick, (int)(firstTick+ticksPerFrame) );
        // Now we process the MidiRules, which in turn process thier GfxActions
        //System.out.System.out.println( "Track " + i + ", numbers of messages = " + trackMessages.size() );
        for ( int j = 0 ; j < trackMessages.size() ; j++ ) {
          // ****************************************
          // TODO : do something with track messages?
          // ****************************************
          //System.out.System.out.println("Track " + i + ", message " + j + " : " + getStringFromShortMessage((ShortMessage)trackMessages.get(j)) );
          /*
          for ( int k = 0 ; k < midiRulesList.length ; k++ ) {
            //System.out.append( "Trying midi rule " + k + " (has track "+midiRulesList[k].getTrack()+") on track " + i + "\n" );
            if ( midiRulesList[k].getTrack() == i ) {
              //System.out.append( "Midi rule " + k + " on track " + i + "\n" );
              for ( int h = 0 ; h < midiRulesList[k].midiSignalCode.size() ; h++ ) {  // might be zero length
                if ( (Integer)midiRulesList[k].midiSignalCode.get(h) == ((ShortMessage)trackMessages.get(j)).getCommand() ) {
                  //System.out.System.out.println("MidiRule hit: track " + i + ", midiRule " + k + ", message number " + j );
                  midiRulesList[k].processMessage( ((ShortMessage)trackMessages.get(j)).getCommand(), 
                  ((ShortMessage)trackMessages.get(j)).getData1(), ((ShortMessage)trackMessages.get(j)).getData2() );
                }
              }
            }
          }
          */
        }
      }
      /*
      // process hold MidiRules
      for ( int i = 0 ; i < midiRulesList.length ; i++ ) {
        if ( midiRulesList[i].isActive() )
          midiRulesList[i].processHold();
      }
      */
    }

    public MidiCodecInfo fillMidiMessageBuffer_bars( int firstBar, int lastBar ) {
      // Get tick range - note, rounding off happens LAST so all ticks stored
      // in double vars. Just cast as int any time it's needed except when
      // calculating the tick value for the end of the range

      double firstTick = ticksPerQuarterNote * (firstBar * quarterNotesPerBar);
      double lastTick = ticksPerQuarterNote * ((lastBar+1) * quarterNotesPerBar);
      lastTick += quarterNotesPerBar; // correction, might not be perfect though

      return fillMidiMessageBuffer_ticks( firstTick, lastTick );
    }


    public MidiCodecInfo fillMidiMessageBuffer_frames( double frameStart, double frameEnd ) {
      // Get tick range - note, rounding off happens LAST so all ticks stored
      // in double vars. Just cast as int any time it's needed except when
      // calculating the tick value for the end of the range

      double firstTick = ticksPerSecond * frameStart;
      double lastTick = ticksPerSecond * frameEnd;

      return fillMidiMessageBuffer_ticks( firstTick, lastTick );
    }


    public MidiCodecInfo fillMidiMessageBuffer_ticks( double tickStart, double tickEnd ) {
      // Get tick range - note, rounding off happens LAST so all ticks stored
      // in double vars. Just cast as int any time it's needed except when
      // calculating the tick value for the end of the range

      MidiCodecInfo midiCodecInfo = new MidiCodecInfo();

      midiCodecInfo.firstTick = tickStart;
      midiCodecInfo.numTicks = tickEnd - tickStart;
      midiCodecInfo.numBars = (float)(midiCodecInfo.numTicks / (ticksPerQuarterNote * quarterNotesPerBar));
      midiCodecInfo.numSecs = (float)(midiCodecInfo.numTicks / ticksPerSecond);

      for ( int i = 0 ; i < numTracks ; i++ ) {
        if ( tracks != null ) {
          if( i >= 0 && i < tracks.length ) {
            Track track = tracks[i];
            for ( int j = 0 ; j < track.size() ; j++ ) {
              MidiEvent event = track.get(j);
              if ( event.getTick() >= tickStart && event.getTick() < tickEnd) {
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                  midiCodecInfo.trackNumber.add( i );
                  midiCodecInfo.message.add( getStringFromShortMessageCommand( ((ShortMessage)message).getCommand()) );
                  midiCodecInfo.value1.add( ((ShortMessage)message).getData1() );
                  midiCodecInfo.value2.add( ((ShortMessage)message).getData2() );
                  midiCodecInfo.tick.add( (int)event.getTick() );
                }
              }
            }
          }
        }
      }

      return midiCodecInfo;
    }


    public ArrayList getTrackMessages( int trackNumber, int tickRangeStart, int tickRangeEnd ) {
      // This is the return array containing all the MIDI messages
      // of ShortMessage subclass type
      ArrayList shortMessages = new ArrayList();

      if ( tracks != null ) {
        if ( trackNumber >= 0 && trackNumber < tracks.length )
        {
          Track track = tracks[trackNumber];
          //if( tickRangeStart < track.ticks() && tickRangeEnd > track.ticks() ) {
          for ( int j = 0 ; j < track.size() ; j++ ) {
            MidiEvent event = track.get(j);
            if ( event.getTick() >= tickRangeStart && event.getTick() < tickRangeEnd ) {
              MidiMessage message = event.getMessage();
              if (message instanceof ShortMessage) {
                //System.out.System.out.println( "ShortMessage at tick " + event.getTick() + " is " +  
                //      getStringFromShortMessage( (ShortMessage)message ) );
                shortMessages.add( (ShortMessage)message );
              }
            }
          }
          //}
        }
      }

      return shortMessages;
    }


    /**
     * 
     * @param noteVal
     * @return 
     */
    public String noteValToString( int noteVal ) {
      String noteStr = Integer.toString(noteVal);

      if ( noteVal >= 0 && noteVal < 128 ) {
        int noteOctave = (noteVal / 12)-1;
        // This gives us the octave because we get the note number divided
        // by 12 WITOUT the remainder. Now to look at what note it is
        int noteLetterVal = noteVal % 12;
        String noteLetter = NOTE_NAMES[noteLetterVal];
        noteStr = noteLetter + noteOctave;
      }

      return noteStr;
    }

    public int StringToNoteVal( String noteStr ) {
      String letterStr = new String(noteStr.substring(0, noteStr.length()-1));
      String octaveStr = new String(noteStr.substring(noteStr.length()-1, noteStr.length()));
      int octave = Integer.valueOf(octaveStr);

      for ( int i = 0 ; i < NOTE_NAMES.length ; i++ ) {
        if ( letterStr.compareToIgnoreCase(NOTE_NAMES[i]) == 0 ) {
          System.out.println( noteStr + " : '" + letterStr + "', '" + octaveStr + "' (" + octave + ") = " + ((octave*12)+i) );
          return (((octave+1)*12)+i);
        }
      }

      return -1;
    }

    public String getStringFromShortMessageCommand( int command ) {
      String outStr = new String();
      switch( command )
      {
      case ShortMessage.NOTE_ON:
        outStr += "Note On";
        break;
      case ShortMessage.NOTE_OFF:   // NOTE OFF
        outStr += "Note Off";
        break;
      case ShortMessage.CONTROL_CHANGE:   // CONTROL CHANGE
        outStr += "Control Change";
        break;
      case ShortMessage.PROGRAM_CHANGE:   // PROGRAM CHANGE
        outStr += "Program Change";
        break;
      case ShortMessage.CHANNEL_PRESSURE:   // CHANNEL PRESSURE
        outStr += "Channel Pressure";
        break;
      case ShortMessage.PITCH_BEND:   // PITCH WHEEL
        outStr += "Pitch Wheel";
        break;
      default:
        outStr += "No Command";
      }
      return outStr;
    }

    public String getStringFromShortMessage( ShortMessage message ) {
      String outStr = new String();
      switch( message.getCommand() )
      {
      case ShortMessage.NOTE_ON:
        // byte 1: key(note) number
        // byte 2: velocity
        outStr += "Note On";
        outStr += " : " + noteValToString( message.getData1() );
        outStr += ", " + message.getData2();
        break;
      case ShortMessage.NOTE_OFF:   // NOTE OFF
        // byte 1: key(note) number
        // byte 2: velocity
        outStr += "Note Off";
        outStr += " : " + noteValToString( message.getData1() );
        outStr += ", " + message.getData2();
        break;
      case ShortMessage.CONTROL_CHANGE:   // CONTROL CHANGE
        // byte 1: controller number (0-119)
        // byte 2: controller value (0-127)
        outStr += "Control Change";
        outStr += " : Ctrl# " + message.getData1();
        outStr += " - " + message.getData2();
        break;
      case ShortMessage.PROGRAM_CHANGE:   // PROGRAM CHANGE
        // byte 1: program number
        outStr += "Program Change";
        outStr += " : Prgm# " + message.getData1();
        break;
      case ShortMessage.CHANNEL_PRESSURE:   // CHANNEL PRESSURE
        // byte 1: pressure
        outStr += "Channel Pressure";
        outStr += " : Pressure " + message.getData1();
        break;
      case ShortMessage.PITCH_BEND:   // PITCH WHEEL
        // byte 1: least significant 7 bits of 14 bit number
        // byte 2: most significant 7 bits of 14 bit number
        outStr += "Pitch Wheel";
        outStr += " : Pitch " + ((message.getData2()<<7)+message.getData1());
        break;
      default:
        outStr += "Error, no message found!";
      }
      return outStr;
    }



    // Private internal helper functions
    /** divisionTypeToString - decode division type data to text
     * 
     * @param type  - type (from javax.sound.midi.Sequence class)
     * @param res   - resolution (from javax.sound.midi.Sequence class)
     * @return      - returns String containing explanatory text, otherwise
     *                  String with error message
     */
    private String divisionTypeToString( float type, int res ) {
      if ( type == Sequence.PPQ ) {
        ticksPerQuarterNote = res;
        return res + " ticks per quarter note";
      } 
      else if ( type == Sequence.SMPTE_24 ) {
        return "SMPTE 24 frames per second, " + res + " ticks per frame";
      } 
      else if ( type == Sequence.SMPTE_25 ) {
        return "SMPTE 25 frames per second, " + res + " ticks per frame";
      } 
      else if ( type == Sequence.SMPTE_30DROP ) {
        return "SMPTE 29.97 frames per second, " + res + " ticks per frame";
      } 
      else if ( type == Sequence.SMPTE_30 ) {
        return "SMPTE 30 frames per second, " + res + " ticks per frame";
      }
      return "No Division Type";
    }
}
