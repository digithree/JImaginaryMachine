/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simonkenny
 */
public class GraphType {
    public static final int GRAPH = 0;
    public static final int IN_NODE = 1;
    public static final int OUT_NODE = 2;
    
    SetCollection inputCollection;
    SetCollection outputCollection;
    GraphNode []inNodes;
    GraphNode []outNodes;
    String name;
    int type;
    
    public GraphType() {
        name = "[GraphType]";
    }
    
    // setters
    public GraphType setType(int _type) {
        type = _type;
        return this;
    }
    
    public GraphType setTypeInteger(Integer _type) {
        int oldType = type;
        type = _type.intValue();
        fire("type", oldType, type);
        return this;
    }
    
    public GraphType setName(String _name) {
        String oldName = name;
        name = _name;
        fire("name", oldName, name);
        return this;
    }
    
    public GraphType setInputCollection(SetCollection in) {
        inputCollection = in;
        inNodes = null;
        if( inputCollection != null ) {
            inNodes = inputCollection.generateAllNodes();
        }
        return this;
    }
    
    public GraphType setOutputCollection(SetCollection out) {
        outputCollection = out;
        outNodes = null;
        if( outputCollection != null ) {
            outNodes = outputCollection.generateAllNodes();
        }
        return this;
    }
    
    // getters
    public int getType() {
        return type;
    }
    
    public Integer getTypeInteger() {
        return new Integer(type);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public SetCollection getInputCollection() {
        return inputCollection;
    }
    
    public SetCollection getOutputCollection() {
        return outputCollection;
    }
    
    public GraphNode[] getInGraphNodes() {
        return inNodes;
    }
    
    public GraphNode[] getOutGraphNodes() {
        return outNodes;
    }
    
    public GraphNode[] getAllGraphNodes() {
        if( inNodes != null && outNodes == null ) {
            return inNodes;
        } else if( inNodes == null && outNodes != null ) {
            return outNodes;
        } else if( inNodes != null && outNodes != null ) {
            GraphNode[] retNodes = new GraphNode[inNodes.length+outNodes.length];
            System.arraycopy(inNodes, 0, retNodes, 0, inNodes.length);
            System.arraycopy(outNodes, 0, retNodes, inNodes.length, outNodes.length);
            return retNodes;
        }
        return null;
    }
    
    private List listeners = Collections.synchronizedList(new LinkedList());

    public void addPropertyChangeListener (PropertyChangeListener pcl) {
        listeners.add (pcl);
    }

    public void removePropertyChangeListener (PropertyChangeListener pcl) {
        listeners.remove (pcl);
    }

    private void fire (String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent (this, propertyName, old, nue));
        }
    }
}
