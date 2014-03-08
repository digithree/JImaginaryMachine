/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 *
 * @author simonkenny
 */
public class GraphData {
    public static Graph graph = null;
    public static List<Observer> observers = new ArrayList<Observer>();
    
    public static void setGraph(Graph _graph) {
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
        }
    }
    
    public static Graph getGraph() {
        return graph;
    }
    
    public static void addObserver(Observer ob) {
        observers.add(ob);
        if( graph != null ) {
            graph.addObserver(ob);
        }
    }
    
    public static boolean removeObserver(Observer ob) {
        graph.deleteObserver(ob);
        return observers.remove(ob);
    }
}
