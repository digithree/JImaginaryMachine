/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.api.GraphNodeResource;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author simonkenny
 */
@ServiceProvider(service=GraphNodeResource.class)
public class MidiPhraseGraphNodeResource implements GraphNodeResource {
    public String[] names = {
        "MidiKeyModifyNode",
        "MidiKeyTypeModifyNode",
        "MidiModeModifyNode",
        "MidiNoteSampleNode",
        "MidiNoteWriteNode",
        "MidiPhraseChooseModifyNode",
        "MidiRangeModifyNode"
    };
    
    public int[] types = {
        GraphNode.MODIFY,
        GraphNode.MODIFY,
        GraphNode.MODIFY,
        GraphNode.SAMPLE,
        GraphNode.WRITE,
        GraphNode.MODIFY,
        GraphNode.MODIFY
    };
    
    public int[] numParams = {
        1,
        1,
        1,
        2,
        1,
        1,
        3
    };
    
    public int[] numConns = {
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
        GraphNode retNodes[] = new GraphNode[7];
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
        GraphNodeInfo retInfos[] = new GraphNodeInfo[7];
        for( int i = 0 ; i < 7 ; i++ ) {
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
}
