/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

/**
 *
 * @author simonkenny
 */
public interface GraphResource {
    // General
    public abstract String getResourceName();
    public abstract String getFriendlyName();
    public abstract String getDescription();
    // SetCollection
    public abstract SetCollection[] getIOSetCollections();
    public abstract SetCollection getInputSetCollection();
    public abstract SetCollection getOutputSetCollection();
    // TODO : set settings for input setcollections, parameters, etc.
    // GraphNode
    public abstract GraphNode[] getAllNodes();
    public abstract GraphNodeInfo[] getAllNodeInfo();
    public abstract GraphNode[] getManditoryFirstModifyNodes();
}
