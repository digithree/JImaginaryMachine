/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.midi.phrase.node.MidiModeModifyNode;
import com.jimaginary.machine.midi.phrase.node.MidiPhraseChooseModifyNode;
import com.jimaginary.machine.midi.phrase.node.MidiKeyTypeModifyNode;
import com.jimaginary.machine.midi.phrase.node.MidiNoteWriteNode;
import com.jimaginary.machine.midi.phrase.node.MidiRangeModifyNode;
import com.jimaginary.machine.midi.phrase.node.MidiKeyModifyNode;
import com.jimaginary.machine.midi.phrase.node.MidiNoteSampleNode;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.api.GraphResource;
import com.jimaginary.machine.api.SetCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author simonkenny
 */
@ServiceProvider(service=GraphResource.class)
public class MidiPhraseGraphNodeResource implements GraphResource {
    
    private final int NUM_NODES = 7;
    
    private final String[] names = {
        "MidiKeyModifyNode",
        "MidiKeyTypeModifyNode",
        "MidiModeModifyNode",
        "MidiNoteSampleNode",
        "MidiNoteWriteNode",
        "MidiPhraseChooseModifyNode",
        "MidiRangeModifyNode"
    };
    
    private final int[] types = {
        GraphNode.MODIFY,
        GraphNode.MODIFY,
        GraphNode.MODIFY,
        GraphNode.SAMPLE,
        GraphNode.WRITE,
        GraphNode.MODIFY,
        GraphNode.MODIFY
    };
    
    private final int[] numParams = {
        1,
        1,
        1,
        2,
        1,
        1,
        3
    };
    
    private final int[] numConns = {
        1,
        1,
        1,
        1,
        1,
        1,  
        1
    };
    
    @Override
    public GraphNode[] getAllNodes() {
        GraphNode retNodes[] = new GraphNode[NUM_NODES];
        retNodes[0] = (GraphNode)new MidiNoteSampleNode();
        retNodes[1] = (GraphNode)new MidiKeyTypeModifyNode();
        retNodes[2] = (GraphNode)new MidiKeyModifyNode();
        retNodes[3] = (GraphNode)new MidiModeModifyNode();
        retNodes[4] = (GraphNode)new MidiRangeModifyNode();
        retNodes[5] = (GraphNode)new MidiNoteWriteNode();
        retNodes[6] = (GraphNode)new MidiPhraseChooseModifyNode();
        return retNodes;
    }

    @Override
    public GraphNodeInfo[] getAllNodeInfo() {
        GraphNodeInfo retInfos[] = new GraphNodeInfo[NUM_NODES];
        for( int i = 0 ; i < NUM_NODES ; i++ ) {
            retInfos[i] = new GraphNodeInfo(-1,names[i],types[i],numParams[i],numConns[i]);
        }
        return retInfos;
    }
    
    @Override
    public GraphNode[] getManditoryFirstModifyNodes() {
        GraphNode retNodes[] = new GraphNode[4];
        retNodes[0] = (GraphNode)new MidiKeyTypeModifyNode();
        retNodes[1] = (GraphNode)new MidiKeyModifyNode();
        retNodes[2] = (GraphNode)new MidiModeModifyNode();
        retNodes[3] = (GraphNode)new MidiRangeModifyNode();
        return retNodes;
    }
    
    public String[] getAllNodeNames() {
        return names;
    }

    @Override
    public String getResourceName() {
        return "MidiPhraseGraphNodeResource";
    }

    @Override
    public String getFriendlyName() {
       return "Midi Phrase Composer";
    }

    @Override
    public String getDescription() {
        return "Enables the composition of diatonic phrases in either tonal, modal or atonal styles which are encoded in MIDI";
    }

    @Override
    public SetCollection[] getIOSetCollections() {
        SetCollection ioSets[] = new SetCollection[2];
        ioSets[0] = new MidiPhraseInputSetCollection();
        ioSets[1] = new MidiPhraseOutputSetCollection();
        return ioSets;
    }

    @Override
    public SetCollection getInputSetCollection() {
        return new MidiPhraseInputSetCollection();
    }

    @Override
    public SetCollection getOutputSetCollection() {
        return new MidiPhraseOutputSetCollection();
    }
}
