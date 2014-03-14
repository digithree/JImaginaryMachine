/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.generalgraph;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.START;

/**
 *
 * @author simonkenny
 */
public class StartNode extends GraphNode {
    public StartNode() {
        super("Start",START,0,1);
        setAcceptsConnection(false);
        setDescription( "Process starts" );
    }

    // don't need processing, but just for console output
    @Override
    public GraphNode process( Graph.GraphPacket graphPacket ) {
        System.out.println( "\n\n"+getName() +" \t\t- starting processing ");
        return getNext(0);
    }

    @Override
    public void refreshDescription() {
        setDescription( "Process starts" );
    }
}