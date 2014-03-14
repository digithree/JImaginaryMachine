/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.*;
import com.digithree.codecs.midi.MidiCodec;
import com.digithree.codecs.midi.MidiPlayer;
import java.io.File;

/**
 *
 * @author simonkenny
 */
public final class MidiPhraseOutputSetCollection extends SetCollection {
    MidiCodec midiCodec;
    MidiPlayer midiPlayer;
    int numPhrases, phraseLength;

    public MidiPhraseOutputSetCollection() {
        super();
        name = "MidiPhraseOutputSetCollection";
        numPhrases = MidiModalConstants.DEFAULT_NUM_OUPTUT_PHRASES;
        phraseLength = MidiModalConstants.DEFAULT_PHRASE_LENGTH;
        setsFollowCurrentHead = false;
        init();
    }
    
    // registration with system
    @Override
    public String getName() {
        return "MidiPhraseOutputSetCollection";
    }
    
    @Override
    public String getGraphNodeResourceName() {
        return "MidiPhraseGraphNodeResource";
    }

    @Override
    public void init() {
        if( midiPlayer != null ) {
                midiPlayer.stop();
        }
        midiPlayer = new MidiPlayer(phraseLength);
        for( int i = 0 ; i < numPhrases ; i++ ) {
                Set set = new Set("MIDI-Phrase-"+(i+1),phraseLength,phraseLength,Set.DISPLAY_VALUES);
                set.outOfBoundsWriteProtect = true;
                sets.add( set );

        }
        currentSet = sets.get(0);
        midiCodec = new MidiCodec();
    }
    

    @Override
    public void outputSet( int idx, String filename) {
        int useIdx = idx % sets.size();
        while(useIdx < 0) {
                useIdx += sets.size();
        }
        //System.out.println(name+":outputSet called - not defined for this collection");
        // DO OUTPUT TO FILE
        // note: if idx == -1, choose all ; if idx >= 0 && < numSets, choose current set only ; otherwise fail
        // ONLY OUTPUT ALL SEQUENCES
        midiPlayer = new MidiPlayer(phraseLength);
        for( Set set : sets ) {
                midiPlayer.addTrack(set);
        }
        String path = "/"; //get path from somewhere!
        midiCodec.writeSequenceToFile(midiPlayer.getSequence(),new File(path+filename+".mid"));
    }

    @Override
    public void playback( int flag, int idx ) {
        // PLAY
        // note: if idx == -1, choose all ; if idx >= 0 && < numSets, choose current set only ; otherwise fail
        if( flag == PLAY || flag == PLAY_LOOPING ) {
            if( midiPlayer != null ) {
                midiPlayer.stop();
            }
            midiPlayer = new MidiPlayer(phraseLength);
            if( idx == -1 ) {
                for( Set set : sets ) {
                    midiPlayer.addTrack(set);
                }
                System.out.println(name+": playback : all phrases");
            } else if( idx >= 0 && idx < sets.size() ) {
                midiPlayer.addTrack(sets.get(idx));
                System.out.println(name+": playback : phrase "+(idx+1));
            } else {
                return;
            }
            if( flag == PLAY_LOOPING ) {
                midiPlayer.setLooping(true);
            } else {
                midiPlayer.setLooping(false);
            }
            midiPlayer.play();
        } else if( flag == STOP ) {
            if( midiPlayer != null ) {
                midiPlayer.stop();
            }
        }
    }

    @Override
    public void display() {
        for( Set set : sets ) {
            midiCodec.displaySetAsString(set);
        }
    }
}
