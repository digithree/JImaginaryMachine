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
public class GraphNodeInfo {
    boolean active;
    int id;
    int type;
    String name;
    String []paramsAsStr;
    String []connectedTo;

    public GraphNodeInfo(int id, String name, int type, int numParameters, int numConnections ) {
        this.id = id;
        this.name = name;
        this.type = type;
        paramsAsStr = new String[numParameters];
        connectedTo = new String[numConnections];
        active = true;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public String getGraphNodeName() {
        return name;
    }
    
    public String getName() {
        return name+"-"+id;
    }

    public int getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public boolean setParameter(int idx, String param) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            paramsAsStr[idx] = param;
            return true;
        }
        return false;
    }
    
    public String getParameter(int idx) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            return paramsAsStr[idx];
        }
        return null;
    }
    
    public int getNumParameters() {
        return paramsAsStr.length;
    }
    
    public boolean setConnectedTo(int idx, String targetNode) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            connectedTo[idx] = targetNode;
            return true;
        }
        return false;
    }
    
    public String getConnectedTo(int idx) {
        if( idx >= 0 && idx < connectedTo.length ) {
            return connectedTo[idx];
        }
        return null;
    }
    
    public int getNumConnections() {
        return connectedTo.length;
    }
    
    public boolean isConnectedTo(String targetNode) {
        for( String str : connectedTo ) {
            if( str.equals(targetNode) ) {
                return true;
            }
        }
        return false;
    }
    
    // if active is false then node whos info this is has been removed
    // this info should be freed then
    public void setActive(boolean flag) {
        active = flag;
    }
    
    public boolean isActive() {
        return active;
    }
}