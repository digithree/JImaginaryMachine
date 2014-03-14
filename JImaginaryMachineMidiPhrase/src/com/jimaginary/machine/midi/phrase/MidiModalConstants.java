/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

/**
 *
 * @author simonkenny
 */
public class MidiModalConstants {
    /*
    Ionian	Tone		Tone		Semitone	Tone		Tone		Tone		Semitone
    Dorian	Tone		Semitone	Tone		Tone		Tone		Semitone	Tone
    Phrygian	Semitone	Tone		Tone		Tone		Semitone	Tone		Tone
    Lydian	Tone		Tone		Tone		Semitone	Tone		Tone		Semitone
    Mixolydian	Tone		Tone		Semitone	Tone		Tone		Semitone	Tone
    Aeolian	Tone		Semitone	Tone		Tone		Semitone	Tone		Tone
    Locrian	Semitone	Tone		Tone		Semitone	Tone		Tone		Tone
    */

    public final static String []KEY_NAMES = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    public final static String []MODE_NAMES = { "Ionian", "Dorian", "Phrygian", "Lydian", "Mixolydian", "Aeolian", "Locrian" };

    public final static int NUM_KEYS = 12;

    public final static int KEY_C = 0;
    public final static int KEY_CS = 1;
    public final static int KEY_D = 2;
    public final static int KEY_DS = 3;
    public final static int KEY_E = 4;
    public final static int KEY_F = 5;
    public final static int KEY_FS = 6;
    public final static int KEY_G = 7;
    public final static int KEY_GS = 8;
    public final static int KEY_A = 9;
    public final static int KEY_AS = 10;
    public final static int KEY_B = 11;

    public final static int NUM_MODES = 7;

    public final static int MODE_IONIAN = 0;
    public final static int MODE_DORIAN = 1;
    public final static int MODE_PHRYGIAN = 2;
    public final static int MODE_LYDIAN = 3;
    public final static int MODE_MIXOLYDIAN = 4;
    public final static int MODE_AEOLIAN = 5;
    public final static int MODE_LOCRIAN = 6;

    public final static int [][]KEY_MODES = {
            { 0, 2, 4, 5, 7, 9, 11 },	// Ionian	- major (high)
            { 0, 2, 3, 5, 7, 9, 10 },	// Dorian	- popular (high)
            { 0, 1, 3, 5, 7, 8, 10 },	// Phrygian	- oriental (med high)
            { 0, 2, 4, 6, 7, 9, 11 },	// Lydian	- odd major [aug 4th] (med)
            { 0, 2, 4, 5, 7, 9, 10 },	// Mixolydian	- odd major [flat 7th] (med)
            { 0, 2, 3, 5, 7, 8, 10 },	// Aeolian	- nat. minor (high)
            { 0, 1, 3, 5, 6, 8, 10 }, 	// Locrian	- strange (low)
    };

    public final static int NUM_MODE_NOTES = 7;
    public final static int []MODE_SUBSCR_STRONG = { 0, 2, 4 };
    public final static int []MODE_SUBSCR_WEAK = { 1, 3, 5, 6 };
    public final static int NUM_OCTAVES = 10; // -1 to 8
    public final static int NOTE_C0 = 24;
    
    public final static int SET_ALL = 0;
    public final static int SET_KEY_ALL = 1;
    public final static int SET_KEY_STRONG = 2;
    public final static int SET_KEY_WEAK = 3;
    
    public final static int DEFAULT_NUM_OUPTUT_PHRASES = 1;
    public final static int DEFAULT_PHRASE_LENGTH = 16;
}
