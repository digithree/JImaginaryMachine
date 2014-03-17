/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.api.GraphResourceService;
import java.util.ArrayList;
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
            System.out.println("SelectionListItemNodeChildFactory : createKeys - creating top level");
            // this is root node, create package type list
            List<SelectionListItem> items = new ArrayList<SelectionListItem>();
            List<String> graphNames = GraphResourceService.getInstance().getAllGraphNodeResPackNames();
            List<String> graphFriendlyNames = GraphResourceService.getInstance().getAllGraphFriendlyNames();
            List<String> graphDescriptions = GraphResourceService.getInstance().getAllGraphDescriptions();
            for( int i = 0 ; i < graphNames.size() ; i++ ) {
                items.add( new SelectionListItem()
                        .setName(graphFriendlyNames.get(i))
                        .setResPackName(graphNames.get(i))
                        .setType(SelectionListItem.GRAPH)     // root node type
                        .setDescription(graphDescriptions.get(i))
                );
                System.out.println("*** resPackName: "+graphNames.get(i));
            }
            System.out.println("SelectionListItemNodeChildFactory : createKeys - created "+graphNames.size() + " graphs");
            listToPopulate.addAll(items);
        } else {
            System.out.println("SelectionListItemNodeChildFactory : creating sub level");
            List<GraphNodeInfo> nodeInfoList = GraphResourceService.getInstance().getAllGraphNodesInfo(item.getResPackName(),false);
            if( !nodeInfoList.isEmpty() ) {
                List<SelectionListItem> nodeList = new ArrayList<SelectionListItem>();
                int count = 0;
                for( GraphNodeInfo info : nodeInfoList ) {
                    nodeList.add( new SelectionListItem()
                                    .setName(info.getGraphNodeName())
                                    .setType(info.getType())
                                );
                }
                System.out.println("SelectionListItemNodeChildFactory : created "+nodeInfoList.size()+" nodes");
                listToPopulate.addAll(nodeList);
            } else {
                System.out.println("SelectionListItemNodeChildFactory : couldn't create any nodes");
            }
        }
        System.out.println("SelectionListItemNodeChildFactory : finished a pass");
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
