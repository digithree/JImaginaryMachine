/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.graph.module.reg.GraphFactory;
import com.jimaginary.machine.api.GraphType;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/**
 * Contains information about a SetCollection group
 * @author simonkenny
 */
public class GraphTypeNodeChildFactory extends ChildFactory<GraphType>
            implements HelpCtx.Provider {
    
    private final GraphType gtp;
    
    GraphTypeNodeChildFactory() {
        gtp = null;
    }
    
    GraphTypeNodeChildFactory(GraphType _gtp) {
        gtp = _gtp;
    }

    private final String[] graphTypeNames = {
        "Midi Phrase Builder(Modal)",
        "Midi Phrase Structurer",
        "PCM Concat Synth",
        "Gamelan Phrase Builder"
    };
    
    @Override
    protected boolean createKeys(List<GraphType> listToPopulate) {
        if( gtp == null ) {
            // this is root node, create package type list
            GraphType []gt = new GraphType[4];
            for( int i = 0 ; i < 4 ; i++ ) {
                gt[i] = GraphFactory.createType(i);
            }
            listToPopulate.addAll(Arrays.asList(gt));
        } else {
            // this is a package type node, populate with GraphNode nodes
            GraphNode []nodes = gtp.getInGraphNodes();
            if( nodes != null ) {
                GraphType []gt = new GraphType[nodes.length];
                for( int i = 0 ; i < nodes.length ; i++ ) {
                    gt[i] = new GraphType().setName(nodes[i].getName()).setType(GraphType.IN_NODE);
                }
                listToPopulate.addAll(Arrays.asList(gt));
            }
            nodes = gtp.getOutGraphNodes();
            if( nodes != null ) {
                GraphType []gt = new GraphType[nodes.length];
                for( int i = 0 ; i < nodes.length ; i++ ) {
                    gt[i] = new GraphType().setName(nodes[i].getName()).setType(GraphType.OUT_NODE);
                }
                listToPopulate.addAll(Arrays.asList(gt));
            }
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(GraphType key) {
        /*
        Node result = new AbstractNode(
            Children.create(new GraphTypeNodeFactory(), true), 
            Lookups.singleton(key));
        result.setDisplayName(key.toString());
        return result;
                */
        return new GraphTypeNode(key);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
