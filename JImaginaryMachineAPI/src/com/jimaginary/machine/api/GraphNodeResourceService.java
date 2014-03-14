/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author simonkenny
 */
public class GraphNodeResourceService {
    private static final GraphNodeResourceService INSTANCE = new GraphNodeResourceService();
    
    protected GraphNodeResourceService() {
        // thwart instantiation
    }
    
    public static GraphNodeResourceService getInstance() {
        return INSTANCE;
    }
    
    // ----
    
    public List<String> getAllGraphNodeResPackNames() {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphNodeResource gnr : gnrList ) {
            names.add(gnr.getResourceName());
        }
        return names;
    }
    
    public List<String> getAllGraphFriendlyNames() {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphNodeResource gnr : gnrList ) {
            names.add(gnr.getFriendlyName());
        }
        return names;
    }
    
    public List<String> getAllGraphDescriptions() {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphNodeResource gnr : gnrList ) {
            names.add(gnr.getDescription());
        }
        return names;
    }
    
    public String getGraphDescriptions(String resPackageName) {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        for( GraphNodeResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) ) {
                return gnr.getDescription();
            }
        }
        return null;
    }
    
    public List<GraphNode> getAllGraphNodes(String resPackageName) {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<GraphNode> nodes = new ArrayList<GraphNode>();
        for( GraphNodeResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) 
                    || gnr.getResourceName().equals("GeneralGraphNodeResource") ) {
                GraphNode []resNodes = gnr.getAllNodes();
                nodes.addAll(Arrays.asList(resNodes));
            }
        }
        return nodes;
    }
    
    public List<GraphNode> getManditoryGraphNodes(String resPackageName) {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<GraphNode> nodes = new ArrayList<GraphNode>();
        for( GraphNodeResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName)
                    || gnr.getResourceName().equals("GeneralGraphNodeResource") ) {
                GraphNode []resNodes = gnr.getManditoryFirstModifyNodes();
                nodes.addAll(Arrays.asList(resNodes));
            }
        }
        return nodes;
    }
    
    public List<GraphNodeInfo> getAllGraphNodesInfo(String resPackageName) {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        List<GraphNodeInfo> nodeInfo = new ArrayList<GraphNodeInfo>();
        for( GraphNodeResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) 
                    || gnr.getResourceName().equals("GeneralGraphNodeResource") ) {
                GraphNodeInfo []resNodeInfo = gnr.getAllNodeInfo();
                nodeInfo.addAll(Arrays.asList(resNodeInfo));
            }
        }
        return nodeInfo;
    }
    
    public GraphNode createGraphNodeByName(String name) {
        Collection<? extends GraphNodeResource> gnrList = Lookup.getDefault().lookupAll(GraphNodeResource.class);
        for( GraphNodeResource gnr : gnrList ) {
            GraphNode []resNodes = gnr.getAllNodes();
            for( GraphNode findNode : resNodes ) {
                if( findNode.getName().equals(name) ) {
                    return findNode;
                }
            }
        }
        return null;
    }
}
