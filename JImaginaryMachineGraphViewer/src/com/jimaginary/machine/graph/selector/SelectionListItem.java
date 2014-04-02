/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simonkenny
 */
public class SelectionListItem {
    public static final int GRAPH = 0;
    public static final int NOT_GRAPH = 1;
    
    String name;
    String resPackName;
    int type;
    String description;
    
    public SelectionListItem() {
        name = "[GraphType]";
    }
    
    // setters
    public SelectionListItem setType(int _type) {
        type = _type;
        return this;
    }
    
    public SelectionListItem setTypeInteger(Integer _type) {
        int oldType = type;
        type = _type.intValue();
        fire("type", oldType, type);
        return this;
    }
    
    public SelectionListItem setName(String _name) {
        String oldName = name;
        name = _name;
        fire("name", oldName, name);
        return this;
    }
    
    public SelectionListItem setResPackName(String _name) {
        resPackName = _name;
        return this;
    }
    
    public SelectionListItem setDescription(String _description) {
        description = _description;
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
    
    public String getResPackName() {
        return resPackName;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    // property change listener
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
