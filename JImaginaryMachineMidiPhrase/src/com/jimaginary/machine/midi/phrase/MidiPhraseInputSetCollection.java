/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.*;
import com.jimaginary.machine.math.Bernoulli;
import com.jimaginary.machine.math.ConstantFunction;
import com.jimaginary.machine.math.Poisson;
import com.jimaginary.machine.math.ProbabilityTable;
import com.jimaginary.machine.math.Uniform;
import java.util.Arrays;

/**
 *
 * @author simonkenny
 */
public final class MidiPhraseInputSetCollection extends SetCollection {
    /*
    Ionian	Tone		Tone		Semitone	Tone		Tone		Tone		Semitone
    Dorian	Tone		Semitone	Tone		Tone		Tone		Semitone	Tone
    Phrygian	Semitone	Tone		Tone		Tone		Semitone	Tone		Tone
    Lydian	Tone		Tone		Tone		Semitone	Tone		Tone		Semitone
    Mixolydian	Tone		Tone		Semitone	Tone		Tone		Semitone	Tone
    Aeolian	Tone		Semitone	Tone		Tone		Semitone	Tone		Tone
    Locrian	Semitone	Tone		Tone		Semitone	Tone		Tone		Tone
    */

    final String []KEY_NAMES = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    final String []MODE_NAMES = { "Ionian", "Dorian", "Phrygian", "Lydian", "Mixolydian", "Aeolian", "Locrian" };

    final int NUM_KEYS = 12;

    final int KEY_C = 0;
    final int KEY_CS = 1;
    final int KEY_D = 2;
    final int KEY_DS = 3;
    final int KEY_E = 4;
    final int KEY_F = 5;
    final int KEY_FS = 6;
    final int KEY_G = 7;
    final int KEY_GS = 8;
    final int KEY_A = 9;
    final int KEY_AS = 10;
    final int KEY_B = 11;

    final int NUM_MODES = 7;

    final int MODE_IONIAN = 0;
    final int MODE_DORIAN = 1;
    final int MODE_PHRYGIAN = 2;
    final int MODE_LYDIAN = 3;
    final int MODE_MIXOLYDIAN = 4;
    final int MODE_AEOLIAN = 5;
    final int MODE_LOCRIAN = 6;

    final int [][]KEY_MODES = {
            { 0, 2, 4, 5, 7, 9, 11 },	// Ionian	- major (high)
            { 0, 2, 3, 5, 7, 9, 10 },	// Dorian	- popular (high)
            { 0, 1, 3, 5, 7, 8, 10 },	// Phrygian	- oriental (med high)
            { 0, 2, 4, 6, 7, 9, 11 },	// Lydian	- odd major [aug 4th] (med)
            { 0, 2, 4, 5, 7, 9, 10 },	// Mixolydian	- odd major [flat 7th] (med)
            { 0, 2, 3, 5, 7, 8, 10 },	// Aeolian	- nat. minor (high)
            { 0, 1, 3, 5, 6, 8, 10 }, 	// Locrian	- strange (low)
    };

    final int NUM_MODE_NOTES = 7;
    final int []MODE_SUBSCR_STRONG = { 0, 2, 4 };
    final int []MODE_SUBSCR_WEAK = { 1, 3, 5, 6 };
    final int NUM_OCTAVES = 10; // -1 to 8
    final int NOTE_C0 = 24;
    
    public final static int SET_ALL = 0;
    public final static int SET_KEY_ALL = 1;
    public final static int SET_KEY_STRONG = 2;
    public final static int SET_KEY_WEAK = 3;

    private final float[] KEY_TYPE_PROBS = { 0.05f, 0.5f, 0.25f, 0.2f };

    private final float []MODIFY_NODE_SELECTION_PROB = { 0.4f, 0.2f, 0.2f, 0.2f };
    public int key, mode;
    
    private static String []nodeNameList = { "MidiNoteSampleNode", 
                                            "MidiKeyTypeModifyNode",
                                            "MidiKeyModifyNode",
                                            "MidiModeModifyNode",
                                            "MidiRangeModifyNode"  
                                           };

    public MidiPhraseInputSetCollection() {
        super();
        name = "MidiPhraseInputSetCollection";
        setsFollowCurrentHead = true;
        setsFollowMode = SETS_FOLLOW_MODE_VALUE_MATCH;
        key = KEY_C;
        mode = MODE_IONIAN;
        init();
    }

    @Override
    public void init() {
        Set set = new Set("Set:All",NUM_OCTAVES*12, 12, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        for( int i = 0 ; i < (NUM_OCTAVES*12) ; i++ ) {
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
    public static String getName() {
        return "MidiPhraseInputSetCollection";
    }
    
    public static String[] getInNodesNames() {
        return nodeNameList;
    }
    
    public static String[] getOutNodesNames() {
        return null;
    }

    @Override
    public void inputSet( String file ) {
        System.out.println("MidiPhraseInputSetCollection::inputSet called - not defined for this collection");
    }

    // Modifiers

    private void createKeyAndModeSet( int _key, int _mode ) {
        if( _key >= 0 && _key < NUM_KEYS ) {
            key = _key;
        }
        if( _mode >= 0 && _mode < NUM_MODES ) {
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
        Set set = new Set("Set:Key",NUM_OCTAVES*NUM_MODE_NOTES, NUM_MODE_NOTES, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        int []offsets = new int[NUM_MODE_NOTES];
        for( int j = 0 ; j < NUM_MODE_NOTES ; j++ ) {
            offsets[j] = (KEY_MODES[mode][j] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < NUM_MODE_NOTES ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        set = new Set("Set:Key-Strong",NUM_OCTAVES*MODE_SUBSCR_STRONG.length, MODE_SUBSCR_STRONG.length, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        offsets = new int[MODE_SUBSCR_STRONG.length];
        for( int j = 0 ; j < MODE_SUBSCR_STRONG.length ; j++ ) {
            offsets[j] = (KEY_MODES[mode][MODE_SUBSCR_STRONG[j]] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < MODE_SUBSCR_STRONG.length ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        set = new Set("Set:Key-Weak",NUM_OCTAVES*MODE_SUBSCR_WEAK.length, MODE_SUBSCR_WEAK.length, Set.DISPLAY_VALUES);
        set.writeHeadMoveByOffset(1);
        offsets = new int[MODE_SUBSCR_WEAK.length];
        for( int j = 0 ; j < MODE_SUBSCR_WEAK.length ; j++ ) {
            offsets[j] = (KEY_MODES[mode][MODE_SUBSCR_WEAK[j]] + key)%12;
        }
        Arrays.sort(offsets); //built in sort! awesome
        for( int i = 0 ; i < NUM_OCTAVES ; i++ ) {
            for( int j = 0 ; j < MODE_SUBSCR_WEAK.length ; j++ ) {
                set.writeValueAndInc( (i*12) + offsets[j] );
            }
        }
        set.readHeadToClosestMatch(note);
        sets.add(set);

        currentSet = sets.get(idx);
    }

    // --- graph building ---
    @Override
    public GraphNode generateSampleNode() {
        return (GraphNode)new MidiNoteSampleNode();
    }

    @Override
    public GraphNode[] generateManditoryFirstModifyNodes() {
        GraphNode retNodes[] = new GraphNode[4];
        retNodes[0] = (GraphNode)new MidiKeyTypeModifyNode();
        retNodes[1] = (GraphNode)new MidiKeyModifyNode();
        retNodes[2] = (GraphNode)new MidiModeModifyNode();
        retNodes[3] = (GraphNode)new MidiRangeModifyNode();
        return retNodes;
    }

    @Override
    public GraphNode generateModifyNode() {
        int select = Utils.getIdxFromProbTable(MODIFY_NODE_SELECTION_PROB);
        if( select == 0 ) { 			// type
                return (GraphNode)new MidiKeyTypeModifyNode();
        } else if( select == 1 ) { 		// key
                return (GraphNode)new MidiKeyModifyNode();
        } else if( select == 2 ) { 		// mode
                return (GraphNode)new MidiModeModifyNode();
        } else if( select == 3 ) { 		// range
                return (GraphNode)new MidiRangeModifyNode();
        }
        return null;
    }
    
    @Override
    public GraphNode[] generateAllNodes() {
        GraphNode retNodes[] = new GraphNode[5];
        retNodes[0] = (GraphNode)new MidiNoteSampleNode();
        retNodes[1] = (GraphNode)new MidiKeyTypeModifyNode();
        retNodes[2] = (GraphNode)new MidiKeyModifyNode();
        retNodes[3] = (GraphNode)new MidiModeModifyNode();
        retNodes[4] = (GraphNode)new MidiRangeModifyNode();
        return retNodes;
    }
    
    @Override
    public GraphNode getGraphNodeByName(String nodeName) {
        if( nodeName.equals(nodeNameList[0]) ) {
            return (GraphNode)new MidiNoteSampleNode();
        } else if( nodeName.equals(nodeNameList[1]) ) {
            return (GraphNode)new MidiKeyModifyNode();
        } else if( nodeName.equals(nodeNameList[2]) ) {
            return (GraphNode)new MidiKeyTypeModifyNode();
        } else if( nodeName.equals(nodeNameList[3]) ) {
            return (GraphNode)new MidiModeModifyNode();
        } else if( nodeName.equals(nodeNameList[4]) ) {
            return (GraphNode)new MidiRangeModifyNode();
        } 
        return null;
    }
    
    // ----***--- NODES ---***----

    // -- Sample GraphNode

    /**
     * MidiNoteSampleNode - samples from a Set
     *
     * params are: jump - number of places to move to sample
     *              size - size of sample
     */
    public class MidiNoteSampleNode extends GraphNode {
        final String []PARAM_JUMP_LIST = { "Random", "Previous", "Next", "+2","+3", "-2","-3" };
        //private final float []PARAM_JUMP_PROBS = { 0.1f, 0.3f, 0.2f, 0.12f, 0.08f, 0.12f, 0.08f };
        final float PARAM_JUMP_MEAN = 2.5f;
        
        private final int PARAM_JUMP = 0;
        private final int PARAM_SIZE = 1;

        MidiNoteSampleNode() {
            super("Midi Note Sample",SAMPLE,2,1);
            setParameter(PARAM_JUMP, new Poisson(PARAM_JUMP_MEAN,PARAM_JUMP_LIST.length-1) );
            setParameter(PARAM_SIZE, new ConstantFunction(1.f) );
        }

        @Override
        public void refreshDescription() {
            setDescription( "jump [" + PARAM_JUMP_LIST[(int)getParameter(PARAM_JUMP).lastValue()]
                + "], size [" + getParameter(PARAM_SIZE).lastValue() + "]");
        }

        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            // calculate offset
            boolean rnd = false;
            int offset = 0;
            if( getParameter(PARAM_JUMP).lastValue() == 0 ) {            // random
                rnd = true;
            } else {
                // if not random, then get offset from last position
                if( getParameter(PARAM_JUMP).lastValue() == 1 ) {     // previous
                    offset = -1;
                } else if( getParameter(PARAM_JUMP).lastValue() == 2 ) {     // next
                    offset = 1;
                } else if( getParameter(PARAM_JUMP).lastValue() >= 3 && getParameter(PARAM_JUMP).lastValue() <= 4) {     // +2, +3
                    offset = (int)getParameter(PARAM_JUMP).lastValue() - 1;
                } else if( getParameter(PARAM_JUMP).lastValue() >= 5 && getParameter(PARAM_JUMP).lastValue() <= 6) {     // -2, -3
                    offset = -1 * ((int)getParameter(PARAM_JUMP).lastValue() - 3);
                }
            }
            // move read head (have no effect if random chosen)
            System.out.println( name+"\t\t- processing: offset = "+offset);
            graphPacket.tempSet = graphPacket.inputSetCollection.sampleSubSet(rnd,offset,(int)getParameter(PARAM_SIZE).lastValue());

            graphPacket.displayVariables();

            return getNext(0);
        }
    }

    // --- Modify Nodes
    public class MidiKeyTypeModifyNode extends GraphNode {
        private final int PARAM_KEY_TYPE = 0;
        //private final float[] PARAM_KEY_TYPE_PROBS = { 0.05f, 0.5f, 0.25f, 0.2f };
        private final String[] PARAM_KEY_TYPE_NAME = { "All", "Key:Any", "Key:Strong", "Key:Weak" };
        private final float mean = 1.65f;

        MidiKeyTypeModifyNode() {
            super("Midi Key Type",MODIFY,1,1);
            setParameter(PARAM_KEY_TYPE, new Poisson(mean,PARAM_KEY_TYPE_NAME.length-1) );
        }

            @Override
        public void refreshDescription() {
            setDescription( "["+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]+"]");
        }

            @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
                ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).chooseSet((int)getParameter(PARAM_KEY_TYPE).lastValue());
            }
            System.out.println(name+"\t\t - set key type to "+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]);
            return getNext(0);
        }
    }

    public class MidiKeyModifyNode extends GraphNode {
        private final int PARAM_KEY = 0;    

        MidiKeyModifyNode() {
            super("Midi Key",MODIFY,1,1);
            setParameter(PARAM_KEY, new Uniform(NUM_KEYS));
        }

        @Override
        public void refreshDescription() {
            setDescription("key: ["+KEY_NAMES[(int)getParameter(PARAM_KEY).lastValue()]+"]");
        }

        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
                ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).createKeyAndModeSet((int)getParameter(PARAM_KEY).lastValue(),-1);
            }

            System.out.println(name+"\t\t\t - set key to "+KEY_NAMES[(int)getParameter(PARAM_KEY).lastValue()]);

            return getNext(0);
        }
    }

    public class MidiModeModifyNode extends GraphNode {
        private final int PARAM_MODE = 0;
        private final float[] PARAM_MODE_PROBS = { 0.2f, 0.2f, 0.14f, 0.1f, 0.1f, 0.2f, 0.06f };
            

        MidiModeModifyNode() {
            super("Midi Mode",MODIFY,1,1);
            setParameter(PARAM_MODE, new ProbabilityTable(PARAM_MODE_PROBS.length));
            getParameter(PARAM_MODE).setParameters(0,PARAM_MODE_PROBS);
            getParameter(PARAM_MODE).setParamNames(0,MODE_NAMES);
        }

            @Override
        public void refreshDescription() {
            setDescription("["+MODE_NAMES[(int)getParameter(PARAM_MODE).lastValue()]+"]");
        }

        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
                ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).createKeyAndModeSet(-1,(int)getParameter(PARAM_MODE).lastValue());
            }

            System.out.println(name+"\t\t\t - set mode to "+MODE_NAMES[(int)getParameter(PARAM_MODE).lastValue()]);

            return getNext(0);
        }
    }

    public class MidiRangeModifyNode extends GraphNode {
        private final int PARAM_OCTAVE = 0;
        private final int PARAM_OCTAVE_RND = 1;
        private final int PARAM_SIZE = 2;
        
        private final float PARAM_OCTAVE_PROB_MEAN = 5; // note: corresponds to octave 3 as mean
        private final String[] PARAM_OCTAVE_NAME = { "Oct -1", "Oct 0", "Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6", "Oct 7" };
        
        private final float PARAM_OCTAVE_PROB_RND = 0.2f;

        private final float PARAM_SIZE_PROB_MEAN = 0.5f;
        private final int PARAM_SIZE_MAX = 2;  //actually 3, including 0 because 0 is invalid size

        MidiRangeModifyNode() {
            super("Midi Range",MODIFY,3,1);
            setParameter(PARAM_OCTAVE, new Poisson(PARAM_OCTAVE_PROB_MEAN,PARAM_OCTAVE_NAME.length-1));
            setParameter(PARAM_OCTAVE_RND, new Bernoulli(PARAM_OCTAVE_PROB_RND));
            setParameter(PARAM_SIZE, new Poisson(PARAM_SIZE_PROB_MEAN,PARAM_SIZE_MAX));
        }

        @Override
        public void refreshDescription() {
            setDescription("range: ["+PARAM_OCTAVE_NAME[(int)getParameter(PARAM_OCTAVE).lastValue()]+"]"
                        +" size: ["+((int)getParameter(PARAM_SIZE).lastValue()+1)+"]");
        }

        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
                graphPacket.inputSetCollection.setRange((int)getParameter(PARAM_OCTAVE).lastValue()==0.f,
                        getParameter(PARAM_OCTAVE).lastValue()-1,getParameter(PARAM_SIZE).lastValue()+1);
            }

            System.out.println(name+"\t\t - set range to "+PARAM_OCTAVE_NAME[(int)getParameter(PARAM_OCTAVE).lastValue()]
                        +", size "+getParameter(PARAM_SIZE).lastValue());

            return getNext(0);
        }
    }
}
