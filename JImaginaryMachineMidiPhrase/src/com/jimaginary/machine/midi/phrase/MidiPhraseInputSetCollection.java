/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.*;
import java.util.Arrays;

/**
 *
 * @author simonkenny
 */
public final class MidiPhraseInputSetCollection extends SetCollection {
    

    private final float[] KEY_TYPE_PROBS = { 0.05f, 0.5f, 0.25f, 0.2f };

    private final float []MODIFY_NODE_SELECTION_PROB = { 0.4f, 0.2f, 0.2f, 0.2f };
    public int key, mode;
    
    public MidiPhraseInputSetCollection() {
        super();
        name = "MidiPhraseInputSetCollection";
        setsFollowCurrentHead = true;
        setsFollowMode = SETS_FOLLOW_MODE_VALUE_MATCH;
        key = MidiModalConstants.KEY_C;
        mode = MidiModalConstants.MODE_IONIAN;
        init();
    }

    @Override
    public void init() {
        Set set = new Set("Set:All",MidiModalConstants.NUM_OCTAVES*12, 12, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        for( int i = 0 ; i < (MidiModalConstants.NUM_OCTAVES*12) ; i++ ) {
                set.writeValueAndInc(i);
        }
        sets.add(set);
        createKeyAndModeSet(-1,-1);
        int idx = Utils.getIdxFromProbTable(KEY_TYPE_PROBS);
        currentSet = sets.get(idx);
        // setting default range
        rangeAll = false;
        rangeStart = 5;		// 3rd octave
        rangeLength = 2;	// two octave in length
    }
    
    // registration with system
    public String getName() {
        return "MidiPhraseInputSetCollection";
    }
    
    @Override
    public String getGraphNodeResourceName() {
        return "MidiPhraseGraphNodeResource";
    }

    @Override
    public void inputSet( String file ) {
        System.out.println("MidiPhraseInputSetCollection::inputSet called - not defined for this collection");
    }

    // Modifiers

    public void createKeyAndModeSet( int _key, int _mode ) {
        if( _key >= 0 && _key < MidiModalConstants.NUM_KEYS ) {
            key = _key;
        }
        if( _mode >= 0 && _mode < MidiModalConstants.NUM_MODES ) {
            mode = _mode;
        }
        float note = -1;
        int idx = 0;
        if( currentSet != null ) {
            note = currentSet.readValue();
            idx = sets.indexOf(currentSet);
            if( idx < 0 ) {
                idx = 0;
            }
            currentSet = null;
        }
        while( sets.size() > 1 ) {
            sets.remove(sets.size()-1);
        }
        // create key/mode sets for all, strong and weak
        Set set = new Set("Set:Key",MidiModalConstants.NUM_OCTAVES*MidiModalConstants.NUM_MODE_NOTES, MidiModalConstants.NUM_MODE_NOTES, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        int []offsets = new int[MidiModalConstants.NUM_MODE_NOTES];
        for( int j = 0 ; j < MidiModalConstants.NUM_MODE_NOTES ; j++ ) {
            offsets[j] = (MidiModalConstants.KEY_MODES[mode][j] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < MidiModalConstants.NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < MidiModalConstants.NUM_MODE_NOTES ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        set = new Set("Set:Key-Strong",MidiModalConstants.NUM_OCTAVES*MidiModalConstants.MODE_SUBSCR_STRONG.length, MidiModalConstants.MODE_SUBSCR_STRONG.length, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        offsets = new int[MidiModalConstants.MODE_SUBSCR_STRONG.length];
        for( int j = 0 ; j < MidiModalConstants.MODE_SUBSCR_STRONG.length ; j++ ) {
            offsets[j] = (MidiModalConstants.KEY_MODES[mode][MidiModalConstants.MODE_SUBSCR_STRONG[j]] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < MidiModalConstants.NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < MidiModalConstants.MODE_SUBSCR_STRONG.length ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        set = new Set("Set:Key-Weak",MidiModalConstants.NUM_OCTAVES*MidiModalConstants.MODE_SUBSCR_WEAK.length, MidiModalConstants.MODE_SUBSCR_WEAK.length, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        offsets = new int[MidiModalConstants.MODE_SUBSCR_WEAK.length];
        for( int j = 0 ; j < MidiModalConstants.MODE_SUBSCR_WEAK.length ; j++ ) {
            offsets[j] = (MidiModalConstants.KEY_MODES[mode][MidiModalConstants.MODE_SUBSCR_WEAK[j]] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < MidiModalConstants.NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < MidiModalConstants.MODE_SUBSCR_WEAK.length ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        currentSet = sets.get(idx);
    }
}
