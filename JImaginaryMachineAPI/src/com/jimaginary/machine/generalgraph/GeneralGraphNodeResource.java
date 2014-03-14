/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.generalgraph;

import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.api.GraphNodeResource;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author simonkenny
 */
@ServiceProvider(service=GraphNodeResource.class)
public class GeneralGraphNodeResource implements GraphNodeResource {
    
    public String[] names = {
        "StartNode",
        "Choice2Node"
    };
    
    public int[] types = {
        GraphNode.START,
        GraphNode.CHOICE
    };
    
    public int[] numParams = {
        0,
        1
    };
    
    public int[] numConns = {
        1,
        2
    };

    @Override
    public String getResourceName() {
        return "GeneralGraphNodeResource";
    }

    @Override
    public GraphNode[] getAllNodes() {
        GraphNode retNodes[] = new GraphNode[7];
        retNodes[0] = (GraphNode)new StartNode();
        retNodes[1] = (GraphNode)new Choice2Node();
        return retNodes;
    }

    @Override
    public GraphNodeInfo[] getAllNodeInfo() {
        GraphNodeInfo retInfos[] = new GraphNodeInfo[2];
        for( int i = 0 ; i < 2 ; i++ ) {
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
    
}
