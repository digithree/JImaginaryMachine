/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * this is a singleton class
 * 
 * @author simonkenny
 */
public class SetData extends Observable {
    private static final SetData INSTANCE = new SetData();
    public List<Set> sets = new ArrayList<Set>();
    
    protected SetData() {
        // thwart instantiation
    }
    
    public static SetData getInstance() {
        return INSTANCE;
    }
    
    // --------
    
    public void addSet(Set set) {
        sets.add(set);
    }
    
    public void addSets(List<Set> otherSets) {
        sets.addAll(otherSets);
    }
    
    public List<Set> getSets() {
        return sets;
    }
    
    public Set getSetByName(String name) {
        for( Set set : sets ) {
            if( set.getName().equals(name) ) {
                return set;
            }
        }
        return null;
    }
    
    public void clearSets() {
        sets.clear();
    }
}
