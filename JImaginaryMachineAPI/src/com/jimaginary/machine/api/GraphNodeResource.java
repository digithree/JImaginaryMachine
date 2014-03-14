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
public interface GraphNodeResource {
    public abstract String getResourceName();
    public abstract String getFriendlyName();
    public abstract String getDescription();
    public abstract GraphNode[] getAllNodes();
    public abstract GraphNodeInfo[] getAllNodeInfo();
    public abstract GraphNode[] getManditoryFirstModifyNodes();
}
