/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import com.jimaginary.machine.math.MathFunction;
import com.jimaginary.machine.math.MathFunctionFactory;
import java.text.ParseException;
import java.util.ArrayList;

/**
 *
 * @author simonkenny
 */
public abstract class GraphNode implements Comparable<GraphNode> {
    static int ID_COUNT = 0;
    
    public final static int START = 0;
    public final static int SAMPLE = 1;
    public final static int WRITE = 2;
    public final static int CHOICE = 3;
    public final static int MODIFY = 4;

    ArrayList<GraphNode> previous;
    GraphNode []next;
    int maxNextConnections;
    MathFunction []parameters;
    int numParameters;
    // flags
    boolean acceptsConnection;
    boolean allowsSelfConnection;
    boolean valid;
    String description;
    
    private final GraphNodeInfo info;

    public GraphNode( String name, int type, int _numParameters, int maxChildren ) {
        info = new GraphNodeInfo(ID_COUNT++,name,type,numParameters,maxChildren);
        
        previous = new ArrayList<GraphNode>();
        maxNextConnections = maxChildren;
        next = new GraphNode[maxNextConnections];
        numParameters = _numParameters;
        parameters = new MathFunction[numParameters];
        acceptsConnection = true;
        allowsSelfConnection = false;
        valid = true;
    }
    
    public GraphNodeInfo getInfo() {
        return info;
    }

    public boolean isInvalid() {
        return !valid;
    }
    
    protected void setAcceptsConnection(boolean flag) {
        acceptsConnection = flag;
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
    public abstract void refreshDescription();

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
    
    // this method will only add next if there is a free space, unlike the
    // other method which you must supply an idx for
    public boolean addNext( GraphNode _next ) {
        if( next != null && _next != null ) {
            for( int i = 0 ; i < next.length ; i++ ) {
                if( next[i] == null ) {
                    next[i] = _next;
                    next[i].previous.add(this);
                    // update info
                    info.setConnectedTo(i, _next.getName() );
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean addNext( int idx, GraphNode _next ) {
        if( next != null ) {
            if( _next != null && idx >= 0 && idx < next.length ) {
                if( _next.acceptsConnection ) {
                    if( !(allowsSelfConnection && this != _next) ) {
                        System.out.println( info.getName()+"\taddNext: added "+_next.getName()+" to next["+idx+"]");
                        next[idx] = _next;
                        next[idx].previous.add(this);
                        // update info
                        info.setConnectedTo(idx, _next.getName() );
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
        info.setActive(false);
    }
    
    public boolean removeNextConnection( GraphNode node ) {
        for( int i = 0 ; i < next.length ; i++ ) {
            if( next[i] == node ) {
                node.previous.remove(this);
                next[i] = null;
                // update info
                info.setConnectedTo(i, null);
                return true;
            }
        }
        return false;
    }

    // PROCESSING - actually process the graph
    protected ArrayList<GraphNode> verify( ArrayList<GraphNode> verifiedNodes ) {
        System.out.println( getName()+"\t\t- verifying...");
        if( !verifiedNodes.contains(this) ) {
            System.out.println( getName()+"\t\t- verified");
            verifiedNodes.add(this);
        }
        System.out.println( getName()+"\t\t- next nodes...");
        for( GraphNode node : next ) {
            if( node != null && node != this &&!verifiedNodes.contains(node) ) {
                System.out.println( getName()+"\t\t- moving to "+node.getName());
                ArrayList<GraphNode> nextVerifiedNodes = node.verify(verifiedNodes);
                System.out.println( getName()+"\t\t- merging...");
                for( GraphNode newNode : nextVerifiedNodes ) {
                    if( !verifiedNodes.contains(newNode) ) {
                        System.out.println( getName()+"\t\t- merged "+newNode.getName());
                        verifiedNodes.add(newNode);
                    }
                }
            } else {
                System.out.println( getName()+"\t\t-    node doesn't exist or is self");
            }
        }
        System.out.println( getName()+"\t\t- return");
        return verifiedNodes;
    }

    // override
    public GraphNode process( Graph.GraphPacket graphPacket ) {
        // do own processing, pass modified packet if no error
        return null;
    }
    
    // IMPORTANT! all nodes must call this at the start of process(...) call
    public boolean updateParametersFromInfoString() throws ParseException {
        boolean madeChange = false;
        for( int i = 0 ; i < numParameters ; i++ ) {
            if( !parameters[i].toString().equals(info.getParameter(i)) ) {
                parameters[i] = MathFunctionFactory.createByParse(info.getParameter(i));
                madeChange = true;
            }
        }
        return madeChange;
    }
    
    // accessors
    // setters
    protected void setId(int _id) {
        info.setId(_id);
    }
    
    protected void setName(String _name) {
        info.setName(_name);
    }
    
    protected void setDescription(String _description) {
        description = _description;
    }
    
    public boolean setParameter(int idx, MathFunction func) {
        if( idx >= 0 && idx < numParameters ) {
            parameters[idx] = func;
            info.setParameter(idx, func.toString());
            if( parametersAreInitialised() ) {
                refreshDescription();
            }
            return true;
        }
        return false;
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
        return info.getType();
    }
    
    public String getName() {
        return info.getName();
    }
    
    public int getId() {
        return info.getId();
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
        return getId() - otherNode.getId();
    }
}
