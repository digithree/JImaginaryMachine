/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.*;
import com.digithree.codecs.midi.MidiCodec;
import com.digithree.codecs.midi.MidiPlayer;
import com.jimaginary.machine.math.Poisson;
import com.jimaginary.machine.math.Uniform;
import java.io.File;

/**
 *
 * @author simonkenny
 */
public final class MidiPhraseOutputSetCollection extends SetCollection {
    MidiCodec midiCodec;
    MidiPlayer midiPlayer;
    int numPhrases, phraseLength;
    
    private static final String []nodeNameList = { "MidiNoteWriteNode", "MidiPhraseChooseModifyNode" };

    public MidiPhraseOutputSetCollection( int _numPhrases, int _phraseLength ) {
        super();
        name = "MidiPhraseOutputSetCollection";
        numPhrases = _numPhrases;
        phraseLength = _phraseLength;
        setsFollowCurrentHead = false;
        init();
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
    
    // registration with system
    public static String getName() {
        return "MidiPhraseOutputSetCollection";
    }
    
    public static String[] getInNodesNames() {
        return null;
    }
    
    public static String[] getOutNodesNames() {
        return nodeNameList;
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

    // --- graph building ---
    @Override
    public GraphNode generateWriteNode() {
        return (GraphNode)new MidiNoteWriteNode();
    }

    // Modifiers
    @Override
    public GraphNode generateModifyNode() {
        return (GraphNode)new MidiPhraseChooseModifyNode(numPhrases);
    }
    
    @Override
    public GraphNode[] generateAllNodes() {
        GraphNode retNodes[] = new GraphNode[2];
        retNodes[0] = (GraphNode)new MidiNoteWriteNode();
        retNodes[1] = (GraphNode)new MidiPhraseChooseModifyNode(numPhrases);
        return retNodes;
    }
    
    @Override
    public GraphNode getGraphNodeByName(String nodeName) {
        if( nodeName.equals(nodeNameList[0]) ) {
            return (GraphNode)new MidiNoteWriteNode();
        } else if( nodeName.equals(nodeNameList[1]) ) {
            return (GraphNode)new MidiPhraseChooseModifyNode(numPhrases);
        }
        return null;
    }
    
    // --- Nodes
    public class MidiNoteWriteNode extends GraphNode {
        final String []PARAM_JUMP_NAMES = { "Next", "+2", "+3", "+4", "+5", "+6" };
        //final float []PARAM_JUMP_PROBS = { 0.65f, 0.24f, 0.05f, 0.03f, 0.02f, 0.01f };
        final float PARAM_JUMP_MEAN = 0.39f;
        
        private final int PARAM_JUMP = 0;

        MidiNoteWriteNode() {
            super("Midi Write",WRITE,1,1);
            setAllowsSelfConnection(true);
            setParameter(PARAM_JUMP, new Poisson(PARAM_JUMP_MEAN,PARAM_JUMP_NAMES.length-1) );
        }

        @Override
        public void refreshDescription() {
            setDescription("jump ["+PARAM_JUMP_NAMES[(int)getParameter(PARAM_JUMP).lastValue()]+"]");
        }

        // override
        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            // get offset
            /*
            boolean rnd = false;
            int offset = 0;
            if( parameters[PARAM_JUMP] == 0 ) {            // random
                rnd = true;
            } else {
                // if not random, then get offset from last position
                if( parameters[PARAM_JUMP] == 1 ) {     // next
                    offset = 1;
                } else if( parameters[PARAM_JUMP] == 2 ) {     // previous
                    offset = -1;
                } else if( parameters[PARAM_JUMP] >= 3 && parameters[PARAM_JUMP] <= 4) {     // +2, +3
                    offset = (int)parameters[PARAM_JUMP] - 1;
                } else if( parameters[PARAM_JUMP] >= 5 && parameters[PARAM_JUMP] <= 6) {     // -2, -3
                    offset = -1 * ((int)parameters[PARAM_JUMP] - 3);
                }
            }
            */
            int offset = (int)getParameter(PARAM_JUMP).lastValue() + 1;

            System.out.println( name+"\t\t\t - process: offset "+offset );

            /*
            if( !rnd ) {
                graphPacket.phraseSet.writeHeadMoveByOffset(offset);
            } else {
                graphPacket.phraseSet.writeHeadMoveToRnd();
            }
            boolean writeSuccess = graphPacket.phraseSet.writeSet(graphPacket.tempSet);
            */

            boolean writeSuccess = graphPacket.outputSetCollection
                    .writeToSet(false,offset,graphPacket.tempSet);

            //graphPacket.displayVariables();

            if( !writeSuccess ) { // Sequence is full
                // FINISHED!
                System.out.println( "-=-=-=-=-=-==== REACHED END OF PHRASE ====-=-=-=-=-=-");
                return null;
            }

            return getNext(0);
        }
    }


    public class MidiPhraseChooseModifyNode extends GraphNode {
        private final int PARAM_PHRASE = 0;

        MidiPhraseChooseModifyNode(int numPhrases) {
            super("Midi Phrase",MODIFY,1,1);
            setParameter(PARAM_PHRASE, new Uniform(numPhrases));
        }

        @Override
        public void refreshDescription() {
            setDescription("Phrase: ["+(int)(getParameter(PARAM_PHRASE).lastValue()+1)+"]");
        }

        @Override
        public GraphNode process( Graph.GraphPacket graphPacket ) {
            if( graphPacket.outputSetCollection instanceof MidiPhraseOutputSetCollection ) {
                ((MidiPhraseOutputSetCollection)graphPacket.outputSetCollection)
                        .chooseSet((int)getParameter(PARAM_PHRASE).lastValue());
            }

            System.out.println(name+"\t\t - choose phrase "+(int)(getParameter(PARAM_PHRASE).lastValue()+1));

            return getNext(0);
        }
    }
}
