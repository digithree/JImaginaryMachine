/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.digithree.codecs.midi;

import com.jimaginary.machine.api.Set;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author simonkenny
 */
public class MidiPlayer {
    final int MIDI_CLICK_SOUND = 42;
    
    public Sequencer sequencer;
    public Sequence sequence;
    public int phraseLength;
    private final boolean initalised;

    int instrumentIdx = 0;
    int instruments[] = {1,8,13};

    public MidiPlayer(int _phraseLength) {
        phraseLength = _phraseLength;
        initalised = addClickTrack();
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void play() {
        if( sequencer != null ) {
            if( sequencer.isRunning() ) {
                sequencer.stop();
            }
            sequencer.setMicrosecondPosition(0);
            sequencer.setTempoInBPM(150);
            sequencer.start();
        }
    }

    public void stop() {
        if( sequencer != null ) {
            if( sequencer.isRunning() ) {
                sequencer.stop();
            }
        }
    }
    
    public boolean isPlaying() {
        if( sequencer != null ) {
            return sequencer.isRunning();
        }
        return false;
    }

    public void setLooping(boolean flag) {
        if( flag ) {
            sequencer.setLoopCount(javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY);
        } else {
            sequencer.setLoopCount(0);
        }
    }

    private boolean addClickTrack() {
        try {
            sequencer = javax.sound.midi.MidiSystem.getSequencer();
        } catch (MidiUnavailableException e) {
            System.err.println("Caught MidiUnavailableException: " + e.getMessage());
            return false;
        }
        if( sequencer == null ) {
            System.out.println( "MidiPlayer: couldn't get sequencer from MidiSystem!!!" );
            return false;
        } else {
            try {
                sequencer.open();
            } catch (MidiUnavailableException e) {
                System.err.println("Caught MidiUnavailableException: " + e.getMessage());
                return false;
            }
        }
        try {		
            sequence = new javax.sound.midi.Sequence(javax.sound.midi.Sequence.PPQ,4);
        } catch( InvalidMidiDataException e ) {
            System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
        }
        int ticksPerBeat = sequence.getResolution();

        Track clickTrack = sequence.createTrack();

        for( int i = 0 ; i < phraseLength ; i++ ) {
            try {
                MidiEvent offEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_OFF,9,MIDI_CLICK_SOUND,0),ticksPerBeat*i);
                clickTrack.add(offEvent);
            } catch( InvalidMidiDataException e ) {
                System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
                return false;
            }
            try {		
                MidiEvent onEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_ON,9,MIDI_CLICK_SOUND,60),ticksPerBeat*i);
                clickTrack.add(onEvent);
            } catch( InvalidMidiDataException e ) {
                System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
                return false;
            }
        }
        try {
            MidiEvent offEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_OFF,9,MIDI_CLICK_SOUND,0),ticksPerBeat*phraseLength);
            clickTrack.add(offEvent);
        } catch( InvalidMidiDataException e ) {
            System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
            return false;
        }

        try {		
            sequencer.setSequence(sequence);
        } catch( InvalidMidiDataException e ) {
            System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
            return false;
        }
        sequencer.setLoopStartPoint(0);
        sequencer.setLoopEndPoint(ticksPerBeat*phraseLength);
        return true;
    }

    public boolean addTrack(Set set) {
        if( !initalised ) {
            return false;
        }

        sequencer.stop();
        int ticksPerBeat = sequence.getResolution();

        Track track = sequence.createTrack();
        // change instrument
        /*
        try {
                MidiEvent event = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, instruments[instrumentIdx], 0),0);
                instrumentIdx = (instrumentIdx+1) % instruments.length;
                track.add(event);
        } catch( InvalidMidiDataException e ) {
                System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
                return false;
        }
        */
        set.readHeadTo(0);
        int lastNote = -1;
        for( int i = 0 ; i < phraseLength ; i++ ) {
            // get next note
            int note = (int)set.readValue();
            if( lastNote != note ) {
                // note off
                try {
                    if( i > 0 && lastNote > 0 ) {  
                        MidiEvent offEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_OFF,lastNote,0),ticksPerBeat*i);
                        track.add(offEvent);
                    }
                } catch( InvalidMidiDataException e ) {
                    System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
                    return false;
                }
                // note on
                try {		
                    if( note > 0 ) { 
                        MidiEvent onEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_ON,note,100),ticksPerBeat*i);
                        track.add(onEvent);
                    }
                } catch( InvalidMidiDataException e ) {
                    System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
                    return false;
                }
            }
            set.readHeadMoveByOffset(1);
            lastNote = note;
        }
        try {
            if( lastNote > 0 ) {
                MidiEvent offEvent = new MidiEvent((MidiMessage)new ShortMessage(ShortMessage.NOTE_OFF,lastNote,0),ticksPerBeat*phraseLength);
                track.add(offEvent);
            }
        } catch( InvalidMidiDataException e ) {
            System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
            return false;
        }

        try {		
            sequencer.setSequence(sequence);
        } catch( InvalidMidiDataException e ) {
            System.err.println("Caught InvalidMidiDataException: " + e.getMessage());
            return false;
        }
        return true;
    }
}
