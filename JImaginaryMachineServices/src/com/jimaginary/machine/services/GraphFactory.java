/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.services;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.midi.phrase.MidiPhraseInputSetCollection;
import com.jimaginary.machine.midi.phrase.MidiPhraseOutputSetCollection;

/**
 *
 * @author simonkenny
 */
public class GraphFactory {
    public final static int GRAPH_GENERAL = 0;
    public final static int GRAPH_MIDI_PHRASE_BUILDER = 1;
    
    public final static int MIN_NUM_NODES = 3;
    public final static int MAX_NUM_NODES = 10;

    
    //private final static String generalGraphNodeResource = "GeneralGraphNodeResource";
        
    
    // TODO : change type for module information, somehow!
    public static Graph createGraph(int type) {
        Graph graph = null;
        if( type == GRAPH_MIDI_PHRASE_BUILDER ) {
            graph = new Graph(new MidiPhraseInputSetCollection(),
                        new MidiPhraseOutputSetCollection());
        }
        return graph;
    }
    
    public static void finishGraph(Graph graph) {
        if( graph != null ) {
            graph.finishChanges();
        }
    }
    
    public static boolean randomiseGraph(Graph graph) {
        if( graph != null ) {
            graph.generateRandom(MIN_NUM_NODES, MAX_NUM_NODES);
            return true;
        }
        return false;
    }
}
