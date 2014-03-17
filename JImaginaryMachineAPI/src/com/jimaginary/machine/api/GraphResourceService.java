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
public class GraphResourceService {
    private static final GraphResourceService INSTANCE = new GraphResourceService();
    
    protected GraphResourceService() {
        // thwart instantiation
    }
    
    public static GraphResourceService getInstance() {
        return INSTANCE;
    }
    
    private GraphResource getGraphResource(String resPackageName) {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        for( GraphResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) ) {
                return gnr;
            }
        }
        return null;
    }
    
    // ----
    
    public List<String> getAllGraphNodeResPackNames() {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphResource gr : gnrList ) {
            names.add(gr.getResourceName());
        }
        return names;
    }
    
    public List<String> getAllGraphFriendlyNames() {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphResource gr : gnrList ) {
            names.add(gr.getFriendlyName());
        }
        return names;
    }
    
    public List<String> getAllGraphDescriptions() {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<String> names = new ArrayList<String>();
        for( GraphResource gr : gnrList ) {
            names.add(gr.getDescription());
        }
        return names;
    }
    
    public String getGraphDescription(String resPackageName) {
        GraphResource gr = getGraphResource(resPackageName);
        if( gr != null ) {
            return gr.getDescription();
        }
        return null;
    }
    
    // SetCollection
    
    public SetCollection[] getIOSetCollections(String resPackageName) {
        GraphResource gr = getGraphResource(resPackageName);
        if( gr != null ) {
            SetCollection ioSets[] = new SetCollection[2];
            ioSets[0] = gr.getInputSetCollection();
            ioSets[1] = gr.getOutputSetCollection();
            return ioSets;
        }
        return null;
    }
    
    public SetCollection getInputSetCollection(String resPackageName) {
        GraphResource gr = getGraphResource(resPackageName);
        if( gr != null ) {
            return gr.getInputSetCollection();
        }
        return null;
    }
    
    public SetCollection getOutputSetCollection(String resPackageName) {
        GraphResource gr = getGraphResource(resPackageName);
        if( gr != null ) {
            return gr.getOutputSetCollection();
        }
        return null;
    }
    
    
    // GraphNode
    
    public List<GraphNode> getAllGraphNodes(String resPackageName, boolean includeGeneralGraph) {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<GraphNode> nodes = new ArrayList<GraphNode>();
        for( GraphResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) 
                    || (includeGeneralGraph && gnr.getResourceName().equals("GeneralGraphNodeResource")) ) {
                GraphNode []resNodes = gnr.getAllNodes();
                if( resNodes != null ) {
                    nodes.addAll(Arrays.asList(resNodes));
                }
            }
        }
        return nodes;
    }
    
    public List<GraphNode> getManditoryGraphNodes(String resPackageName, boolean includeGeneralGraph) {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<GraphNode> nodes = new ArrayList<GraphNode>();
        for( GraphResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) 
                    || (includeGeneralGraph && gnr.getResourceName().equals("GeneralGraphNodeResource")) ) {
                GraphNode []resNodes = gnr.getManditoryFirstModifyNodes();
                if( resNodes != null ) {
                    nodes.addAll(Arrays.asList(resNodes));
                }
            }
        }
        return nodes;
    }
    
    public List<GraphNodeInfo> getAllGraphNodesInfo(String resPackageName, boolean includeGeneralGraph) {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<GraphNodeInfo> nodeInfo = new ArrayList<GraphNodeInfo>();
        for( GraphResource gnr : gnrList ) {
            if( gnr.getResourceName().equals(resPackageName) 
                    || (includeGeneralGraph && gnr.getResourceName().equals("GeneralGraphNodeResource")) ) {
                GraphNodeInfo []resNodeInfo = gnr.getAllNodeInfo();
                if( resNodeInfo != null ) {
                    nodeInfo.addAll(Arrays.asList(resNodeInfo));
                }
            }
        }
        return nodeInfo;
    }
    
    public List<GraphNodeInfo> getAllGraphNodesInfo() {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        List<GraphNodeInfo> nodeInfo = new ArrayList<GraphNodeInfo>();
        for( GraphResource gnr : gnrList ) {
            GraphNodeInfo []resNodeInfo = gnr.getAllNodeInfo();
            if( resNodeInfo != null ) {
                nodeInfo.addAll(Arrays.asList(resNodeInfo));
            }
        }
        return nodeInfo;
    }
    
    public GraphNode createGraphNodeByName(String name) {
        Collection<? extends GraphResource> gnrList = Lookup.getDefault().lookupAll(GraphResource.class);
        for( GraphResource gnr : gnrList ) {
            GraphNode []resNodes = gnr.getAllNodes();
            for( GraphNode findNode : resNodes ) {
                if( findNode != null ) {
                    if( findNode.getInfo().getGraphNodeName().equals(name) ) {
                        return findNode;
                    }
                }
            }
        }
        return null;
    }
}
