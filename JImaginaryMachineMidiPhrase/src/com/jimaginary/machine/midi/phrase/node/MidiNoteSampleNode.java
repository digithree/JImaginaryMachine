/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase.node;

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.math.Bernoulli;
import com.jimaginary.machine.math.ConstantFunction;
import com.jimaginary.machine.math.Poisson;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* MidiNoteSampleNode - samples from a Set
*
* params are: jump - number of places to move to sample
*              size - size of sample
*///MidiNoteSampleNode
public class MidiNoteSampleNode extends GraphNode {
   final String []PARAM_JUMP_LIST = {  "-3","-2", "Previous", "Next", "+2","+3" };
   final float PARAM_JUMP_MEAN = 3.65f;

   private final int PARAM_JUMP = 0;
   private final int PARAM_SIZE = 1;
   private final int PARAM_RND_JUMP = 2;

   public MidiNoteSampleNode() {
       super("MidiNoteSampleNode",SAMPLE,3,1);
       getInfo().setParameterName(PARAM_JUMP, "Jump amount");
       getInfo().setParameterNumIdx(PARAM_JUMP, PARAM_JUMP_LIST.length);
       getInfo().setParameterIdxNames(PARAM_JUMP, PARAM_JUMP_LIST);
       setParameter(PARAM_JUMP, new Poisson(PARAM_JUMP_MEAN,PARAM_JUMP_LIST.length-1) );
       getInfo().setParameterName(PARAM_SIZE, "Sample size");
       getInfo().setParameterNumIdx(PARAM_SIZE, 1);
       setParameter(PARAM_SIZE, new ConstantFunction(1.f) );
       getInfo().setParameterName(PARAM_RND_JUMP, "Rnd jump");
       getInfo().setParameterNumIdx(PARAM_RND_JUMP, 2);
       getInfo().setParameterIdxNames(PARAM_RND_JUMP, new String[]{"Yes","No"});
       setParameter(PARAM_RND_JUMP, new Bernoulli(0.f));
       getParameter(PARAM_RND_JUMP).setAlwaysRandomise(true);
   }

   @Override
   public void refreshDescription() {
       setDescription( "jump [" + PARAM_JUMP_LIST[(int)getParameter(PARAM_JUMP).lastValue()]
           + "], size [" + getParameter(PARAM_SIZE).lastValue() + "]");
   }

   @Override
   public String process( Graph.GraphPacket graphPacket ) {
        // update parameter objects (MathFunction) if info.paramsAsStr[...] changed
        try {
            updateParametersFromInfoString();
        } catch (ParseException ex) {
            Logger.getLogger(MidiKeyModifyNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        // process
        
       // calculate offset
       boolean rnd = ((int)getParameter(PARAM_RND_JUMP).evaluate()) == 0;
       int offset = 0;
       if( !rnd ) {
           offset = -3 + (int)getParameter(PARAM_JUMP).lastValue();
           if( getParameter(PARAM_JUMP).lastValue() > 2 ) {
               offset++;
           }
       }
       // move read head (have no effect if random chosen)
       ConsoleWindowOut.getInstance().println( getName()+"\t\t- processing: offset = "+offset);
       graphPacket.tempSet = graphPacket.inputSetCollection.sampleSubSet(rnd,offset,(int)getParameter(PARAM_SIZE).lastValue());

       graphPacket.displayVariables();

       return getNext(0);
   }
}