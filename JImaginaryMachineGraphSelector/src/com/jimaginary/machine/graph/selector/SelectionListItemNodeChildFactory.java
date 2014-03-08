/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import com.jimaginary.graph.module.reg.GraphFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/**
 * Contains information about a SetCollection group
 * @author simonkenny
 */
public class SelectionListItemNodeChildFactory extends ChildFactory<SelectionListItem>
            implements HelpCtx.Provider {
    
    private final String[] graphTypeNames = {
        "Midi Phrase Builder(Modal)",
        "Midi Phrase Structurer",
        "PCM Concat Synth",
        "Gamelan Phrase Builder"
    };
    
    private final SelectionListItem item;
    
    public SelectionListItemNodeChildFactory() {
        item = null;
    }
    
    public SelectionListItemNodeChildFactory(SelectionListItem _item) {
        item = _item;
    }
    
    @Override
    protected boolean createKeys(List<SelectionListItem> listToPopulate) {
        if( item == null ) {
            // this is root node, create package type list
            SelectionListItem []items = new SelectionListItem[4];
            for( int i = 0 ; i < 4 ; i++ ) {
                items[i] = new SelectionListItem().setName(graphTypeNames[i])
                        .setType(0)     // root node type
                        .setDescription(GraphFactory.getGraphDescription(i))
                        ;
            }
            listToPopulate.addAll(Arrays.asList(items));
        } else {
            // this is a grpah package type node, populate with GraphNode nodes
            String []inNodes = GraphFactory.getInputSetCollectionNodeNames(item.getType());
            String []outNodes = GraphFactory.getOutputSetCollectionNodeNames(item.getType());
            List<String> nodeNameList = new ArrayList<String>();
            if( inNodes != null ) {
                nodeNameList.addAll(Arrays.asList(inNodes));
            }
            if( outNodes != null ) {
                nodeNameList.addAll(Arrays.asList(outNodes));
            }
            if( !nodeNameList.isEmpty() ) {
                List<SelectionListItem> nodeList = new ArrayList<SelectionListItem>();
                int count = 0;
                for( String name : nodeNameList ) {
                    nodeList.add( new SelectionListItem()
                                    .setName(name)
                                    .setType(count++ < inNodes.length?SelectionListItem.IN_NODE:SelectionListItem.OUT_NODE)
                                );
                }
                listToPopulate.addAll(nodeList);
            }
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(SelectionListItem key) {
        /*
        Node result = new AbstractNode(
            Children.create(new GraphTypeNodeFactory(), true), 
            Lookups.singleton(key));
        result.setDisplayName(key.toString());
        return result;
                */
        return new SelectionListItemNode(key);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
