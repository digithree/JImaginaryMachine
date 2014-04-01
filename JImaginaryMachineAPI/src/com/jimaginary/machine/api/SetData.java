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
        System.out.println("SetData:addSet "+set.getName());
        setChanged();
        notifyObservers();
    }
    
    public void addSets(List<Set> otherSets) {
        sets.addAll(otherSets);
        for( Set set : otherSets ) {
            System.out.println("SetData:addSets "+set.getName());
        }
        setChanged();
        notifyObservers();
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
    
    public boolean removeSetByName(String name) {
        Set removeSet = getSetByName(name);
        if( removeSet != null ) {
            sets.remove(removeSet);
            return true;
        }
        return false;
    }
    
    public void clearSets() {
        sets.clear();
        System.out.println("SetData:clear");
        setChanged();
        notifyObservers();
    }
}
