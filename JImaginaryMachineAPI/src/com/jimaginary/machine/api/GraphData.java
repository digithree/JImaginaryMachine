/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.JOptionPane;

/**
 *
 * @author simonkenny
 */
public class GraphData {
    public static Graph graph;
    public static List<Observer> observers = new ArrayList<Observer>();
    public static String currentNodeName;
    public static String lastNodeName;
    
    protected GraphData() {
        // thwart instantiation
    }
    
    public static boolean setGraph(Graph _graph) {
        System.out.println("GraphData:setGraph");
        if( graph != null ) {
            graph.deleteObservers();
        }
        graph = _graph;
        if( graph != null ) {
            for( Observer ob : observers ) {
                System.out.println("added observer to graph");
                graph.addObserver(ob);
            }
            return true;
        }
        return false;
    }
    
    public static Graph getGraph() {
        return graph;
    }
    
    public static boolean addObserver(Observer ob) {
        observers.add(ob);
        if( graph != null ) {
            graph.addObserver(ob);
            return true;
        }
        return false;
    }
    
    public static boolean removeObserver(Observer ob) {
        if( graph != null ) {
            graph.deleteObserver(ob);
        }
        return observers.remove(ob);
    }
    
    public static void removeObservers() {
        if( graph != null ) {
            graph.deleteObservers();
        }
        observers.clear();
    }
    
    public static GraphNode getGraphNodeById(int id) {
        if( graph != null ) {
            return graph.getNodeById(id);
        }
        return null;
    }
    
    // node creation
    public static void setNodeName(String _name) {
        if( _name.contains("Node") || _name.contains("node") ) {
            lastNodeName = currentNodeName;
            currentNodeName = _name;
        }
    }
    
    public static String getNodeName() {
        return currentNodeName;
    }
    
    public static String getLastNodeName() {
        return lastNodeName;
    }
}
