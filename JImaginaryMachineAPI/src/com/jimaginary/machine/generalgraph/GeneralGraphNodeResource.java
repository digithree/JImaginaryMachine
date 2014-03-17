/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.generalgraph;

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
public class GeneralGraphNodeResource implements GraphResource {
    
    private final int NUM_NODES = 2;
    
    private final String[] names = {
        "StartNode",
        "Choice2Node"
    };
    
    private final int[] types = {
        GraphNode.START,
        GraphNode.CHOICE
    };
    
    private final int[] numParams = {
        0,
        1
    };
    
    private final int[] numConns = {
        1,
        2
    };

    @Override
    public String getResourceName() {
        return "GeneralGraphNodeResource";
    }

    @Override
    public GraphNode[] getAllNodes() {
        GraphNode retNodes[] = new GraphNode[NUM_NODES];
        retNodes[0] = (GraphNode)new StartNode();
        retNodes[1] = (GraphNode)new Choice2Node();
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
        return null;
    }

    @Override
    public String getFriendlyName() {
        return "General";
    }

    @Override
    public String getDescription() {
        return "[no graph, general graph elements]";
    }

    @Override
    public SetCollection[] getIOSetCollections() {
        return null;
    }

    @Override
    public SetCollection getInputSetCollection() {
        return null;
    }

    @Override
    public SetCollection getOutputSetCollection() {
        return null;
    }
    
}
