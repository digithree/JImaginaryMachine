/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.graph.module.reg;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphType;
import com.jimaginary.machine.midi.phrase.MidiPhraseInputSetCollection;
import com.jimaginary.machine.midi.phrase.MidiPhraseOutputSetCollection;

/**
 *
 * @author simonkenny
 */
public class GraphFactory {
    public final static int GRAPH_MIDI_PHRASE_BUILDER = 0;
    
    public final static int MIN_NUM_NODES = 3;
    public final static int MAX_NUM_NODES = 5;
    
    public final static int DEFAULT_NUM_PHRASES = 1;
    public final static int DEFAULT_PHRASE_LEN = 16;
    
    private final static String[] graphTypeNames = {
        "Midi Phrase Builder(Modal)",
        "Midi Phrase Structurer",
        "PCM Concat Synth",
        "Gamelan Phrase Builder"
    };
    
    // TODO : change type for module information, somehow!
    public static Graph createGraph(int type) {
        Graph graph = null;
        switch(type) {
            case GRAPH_MIDI_PHRASE_BUILDER:
                graph = new Graph(new MidiPhraseInputSetCollection(),
                            new MidiPhraseOutputSetCollection(DEFAULT_NUM_PHRASES,DEFAULT_PHRASE_LEN));
                break;
        }
        return graph;
    }
    
    public static boolean randomiseGraph(Graph graph) {
        if( graph != null ) {
            graph.generateRandom(MIN_NUM_NODES, MAX_NUM_NODES);
            return true;
        }
        return false;
    }
    
    public static GraphType createType(int type) {
        GraphType gt = new GraphType().setType(GraphType.GRAPH);
        switch(type) {
            case GRAPH_MIDI_PHRASE_BUILDER:
                gt.setName(graphTypeNames[type])
                    .setInputCollection(new MidiPhraseInputSetCollection())
                    .setOutputCollection(new MidiPhraseOutputSetCollection(DEFAULT_NUM_PHRASES,DEFAULT_PHRASE_LEN))
                    ;
                break;
            case 1:
                gt.setName(graphTypeNames[type]);
                break;
            case 2:
                gt.setName(graphTypeNames[type]);
                break;
            case 3:
                gt.setName(graphTypeNames[type]);
                break;
        }       
        return gt;
    }
}
