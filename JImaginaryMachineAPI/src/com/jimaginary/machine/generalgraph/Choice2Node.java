/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.generalgraph;

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.CHOICE;
import com.jimaginary.machine.math.Bernoulli;

/**
 *
 * @author simonkenny
 */
public class Choice2Node extends GraphNode {
    final float NODE_CHOICE_MIN_VAL = 0.15f;

    private final int PARAM_CHOICE = 0;
    private final float CHOICE_PROB = 0.5f;

    public Choice2Node() {
        super("Choice2Node",CHOICE,1,2);
        //default values for choices, uniform
        getInfo().setParameterName(PARAM_CHOICE, "Choice");
        getInfo().setParameterNumIdx(PARAM_CHOICE, 2);
        getInfo().setParameterIdxNames(PARAM_CHOICE, new String[]{"A","B"});
        setParameter(PARAM_CHOICE, new Bernoulli(CHOICE_PROB)); 
        getParameter(PARAM_CHOICE).setAlwaysRandomise(true);
    }

    @Override
    public void refreshDescription() {
        setDescription( "Choose A P["+String.format("%.2f",getParameter(PARAM_CHOICE).getParameter(0))+"] or B P["
            +String.format("%.2f",(1.f-getParameter(PARAM_CHOICE).getParameter(0)))+"]");
    }

    // override
    @Override
    public String process( Graph.GraphPacket graphPacket ) {
        int choice = (int)getParameter(PARAM_CHOICE).evaluate();

        ConsoleWindowOut.getInstance().println(getName()+" \t\t- choose "+(choice==0?"A":"B"));
        return getNext(choice);
    }
}