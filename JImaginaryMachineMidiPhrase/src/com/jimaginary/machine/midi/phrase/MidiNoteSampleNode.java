/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.SAMPLE;
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
   final String []PARAM_JUMP_LIST = { "Random", "Previous", "Next", "+2","+3", "-2","-3" };
   //private final float []PARAM_JUMP_PROBS = { 0.1f, 0.3f, 0.2f, 0.12f, 0.08f, 0.12f, 0.08f };
   final float PARAM_JUMP_MEAN = 2.5f;

   private final int PARAM_JUMP = 0;
   private final int PARAM_SIZE = 1;

   MidiNoteSampleNode() {
       super("Midi Note Sample",SAMPLE,2,1);
       setParameter(PARAM_JUMP, new Poisson(PARAM_JUMP_MEAN,PARAM_JUMP_LIST.length-1) );
       setParameter(PARAM_SIZE, new ConstantFunction(1.f) );
   }

   @Override
   public void refreshDescription() {
       setDescription( "jump [" + PARAM_JUMP_LIST[(int)getParameter(PARAM_JUMP).lastValue()]
           + "], size [" + getParameter(PARAM_SIZE).lastValue() + "]");
   }

   @Override
   public GraphNode process( Graph.GraphPacket graphPacket ) {
        // update parameter objects (MathFunction) if info.paramsAsStr[...] changed
        try {
            updateParametersFromInfoString();
        } catch (ParseException ex) {
            Logger.getLogger(MidiKeyModifyNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        // process
        
       // calculate offset
       boolean rnd = false;
       int offset = 0;
       if( getParameter(PARAM_JUMP).lastValue() == 0 ) {            // random
           rnd = true;
       } else {
           // if not random, then get offset from last position
           if( getParameter(PARAM_JUMP).lastValue() == 1 ) {     // previous
               offset = -1;
           } else if( getParameter(PARAM_JUMP).lastValue() == 2 ) {     // next
               offset = 1;
           } else if( getParameter(PARAM_JUMP).lastValue() >= 3 && getParameter(PARAM_JUMP).lastValue() <= 4) {     // +2, +3
               offset = (int)getParameter(PARAM_JUMP).lastValue() - 1;
           } else if( getParameter(PARAM_JUMP).lastValue() >= 5 && getParameter(PARAM_JUMP).lastValue() <= 6) {     // -2, -3
               offset = -1 * ((int)getParameter(PARAM_JUMP).lastValue() - 3);
           }
       }
       // move read head (have no effect if random chosen)
       System.out.println( getName()+"\t\t- processing: offset = "+offset);
       graphPacket.tempSet = graphPacket.inputSetCollection.sampleSubSet(rnd,offset,(int)getParameter(PARAM_SIZE).lastValue());

       graphPacket.displayVariables();

       return getNext(0);
   }
}