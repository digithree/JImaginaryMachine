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
    String []paramsName;
    int []paramsNumIdx;
    String [][]paramIdxNames;
    String []connectedTo;

    public GraphNodeInfo(int id, String name, int type, int numParameters, int numConnections ) {
        this.id = id;
        this.name = name;
        this.type = type;
        paramsAsStr = new String[numParameters];
        paramsName = new String[numParameters];
        paramsNumIdx = new int[numParameters];
        paramIdxNames = new String[numParameters][];
        connectedTo = new String[numConnections];
        active = true;
        System.out.println("created GraphNodeInfo "+getName()+" with "+numParameters+" parameters");
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
    
    public boolean setParameterName(int idx, String pname) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            paramsName[idx] = pname;
            return true;
        }
        return false;
    }
    
    public boolean setParameterNumIdx(int idx, int num) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            paramsNumIdx[idx] = num;
            paramIdxNames[idx] = new String[num];
            for( int i = 0 ; i < num ; i++ ) {
                paramIdxNames[idx][i] = "idx-"+i;
            }
            return true;
        }
        return false;
    }
    
    public boolean setParameterIdxNames(int idx, String []names) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            if( names.length == paramsNumIdx[idx] ) {
                paramIdxNames[idx] = names;
                return true;
            }
        }
        return false;
    }
    
    // getters 
    public String getParameter(int idx) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            return paramsAsStr[idx];
        }
        return null;
    }
    
    public String getParameterName(int idx) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            return paramsName[idx];
        }
        return null;
    }
    
    public int getParameterNumIdx(int idx) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            return paramsNumIdx[idx];
        }
        return -1;
    }
    
    public String[] getParameterIdxNames(int idx) {
        if( idx >= 0 && idx < paramsAsStr.length ) {
            return paramIdxNames[idx];
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