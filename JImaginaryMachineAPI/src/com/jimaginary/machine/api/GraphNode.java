/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import com.jimaginary.machine.math.MathFunction;
import java.util.ArrayList;

/**
 *
 * @author simonkenny
 */
public class GraphNode implements Comparable<GraphNode> {
    static int ID_COUNT = 0;
    
    public final static int START = 0;
    public final static int SAMPLE = 1;
    public final static int WRITE = 2;
    public final static int CHOICE = 3;
    public final static int MODIFY = 4;

    int id = ID_COUNT++;
    int type;
    ArrayList<GraphNode> previous;
    GraphNode []next;
    int maxNextConnections;
    MathFunction []parameters;
    int numParameters;
    // flags
    boolean acceptsConnection;
    boolean allowsSelfConnection;
    boolean valid;
    String name;
    String description;

    public GraphNode( String _name, int _type, int _numParameters, int maxChildren ) {
        type = _type;
        name = _name;
        previous = new ArrayList<GraphNode>();
        maxNextConnections = maxChildren;
        next = new GraphNode[maxNextConnections];
        numParameters = _numParameters;
        parameters = new MathFunction[numParameters];
        acceptsConnection = true;
        allowsSelfConnection = false;
        valid = true;
    }

    public boolean isInvalid() {
        return !valid;
    }

    protected boolean willAcceptConnection() {
        return acceptsConnection;
    }

    protected boolean willAcceptConnectionFrom(GraphNode nodeTo) {
        if( !acceptsConnection ) {
            return false;
        }
        for( GraphNode node : previous ) {
            if( node == nodeTo ) {
                return false;
            }
        }
        return true;
    }

    protected boolean willAllowSelfConnection() {
        return allowsSelfConnection;
    }

    //override
    public void refreshDescription() {
        // in sub-class: set description text via
        //		setNameAndDescription(null, "description text");
    }

    public void randomiseParameters() {
        for( MathFunction param : parameters ) {
            param.evaluate();
        }
    }

    protected boolean nextNodesSet() {
        for (GraphNode node : next) {
            if (node == null) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean nextContains(GraphNode node) {
        for( int i = 0 ; i < next.length ; i++ ) {
            if( next[i] == node ) {
                return true;
            }
        }
        return false;
    }

    protected boolean addNext( int idx, GraphNode _next ) {
        if( next != null ) {
            if( _next != null && idx >= 0 && idx < next.length ) {
                if( _next.acceptsConnection ) {
                    if( !(allowsSelfConnection && this != _next) ) {
                        System.out.println( name+"\taddNext: added "+_next.name+" to next["+idx+"]");
                        next[idx] = _next;
                        next[idx].previous.add(this);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void remove() {
        for( GraphNode node : previous ) {
            for( int i = 0 ; i < node.next.length ; i++ ) {
                if( node.next[i] == this ) {
                    node.next[i] = null;
                }
            }
        }
        for( GraphNode node : next ) {
            if( node != null ) {
                node.previous.remove(this);
            }
        }
    }

    // PROCESSING - actually process the graph
    protected ArrayList<GraphNode> verify( ArrayList<GraphNode> verifiedNodes ) {
        System.out.println( name+"\t\t- verifying...");
        if( !verifiedNodes.contains(this) ) {
            System.out.println( name+"\t\t- verified");
            verifiedNodes.add(this);
        }
        System.out.println( name+"\t\t- next nodes...");
        for( GraphNode node : next ) {
            if( node != null && node != this &&!verifiedNodes.contains(node) ) {
                System.out.println( name+"\t\t- moving to "+node.name);
                ArrayList<GraphNode> nextVerifiedNodes = node.verify(verifiedNodes);
                System.out.println( name+"\t\t- merging...");
                for( GraphNode newNode : nextVerifiedNodes ) {
                    if( !verifiedNodes.contains(newNode) ) {
                        System.out.println( name+"\t\t- merged "+newNode.name);
                        verifiedNodes.add(newNode);
                    }
                }
            } else {
                System.out.println( name+"\t\t-    node doesn't exist or is self");
            }
        }
        System.out.println( name+"\t\t- return");
        return verifiedNodes;
    }

    // override
    public GraphNode process( Graph.GraphPacket graphPacket ) {
        // do own processing, pass modified packet if no error
        return null;
    }
    
    // accessors
    // setters
    protected void setId(int _id) {
        id = _id;
    }
    
    protected void setName(String _name) {
        name = _name;
    }
    
    protected void setDescription(String _description) {
        description = _description;
    }
    
    protected void setParameter(int idx, MathFunction func) {
        if( idx >= 0 && idx < numParameters ) {
            parameters[idx] = func;
        }
        if( parametersAreInitialised() ) {
            refreshDescription();
        }
    }
    
    protected boolean parametersAreInitialised() {
        for( MathFunction func : parameters ) {
            if( func == null ) {
                return false;
            }
        }
        return true;
    }
    
    protected void setAllowsSelfConnection(boolean state) {
        allowsSelfConnection = state;
    }
    
    // getters
    public int getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
    
    protected String getDescription() {
        return description;
    }
    
    protected int getNumParameters() {
        return numParameters;
    }
    
    public int getNumConnections() {
        return next.length;
    }
    
    public MathFunction getParameter(int idx) {
        if( idx >= 0 && idx < numParameters ) {
            return parameters[idx];
        }
        return null;
    }
    
    public GraphNode getNext(int idx) {
        if( idx >= 0 && idx < next.length ) {
            return next[idx];
        }
        return null;
    }
    
    protected boolean getAllowsSelfConnection() {
        return allowsSelfConnection;
    }

    @Override
    public int compareTo(GraphNode otherNode) {
        return id - otherNode.getId();
    }
}
