/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

// --- Modify Nodes

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.MODIFY;
import com.jimaginary.machine.math.Poisson;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

    public class MidiKeyTypeModifyNode extends GraphNode {
        private final int PARAM_KEY_TYPE = 0;
        //private final float[] PARAM_KEY_TYPE_PROBS = { 0.05f, 0.5f, 0.25f, 0.2f };
        private final String[] PARAM_KEY_TYPE_NAME = { "All", "Key:Any", "Key:Strong", "Key:Weak" };
        private final float mean = 1.65f;

        MidiKeyTypeModifyNode() {
            super("MidiKeyTypeModifyNode",MODIFY,1,1);
            getInfo().setParameterName(PARAM_KEY_TYPE, "Key type");
            getInfo().setParameterNumIdx(PARAM_KEY_TYPE, PARAM_KEY_TYPE_NAME.length);
            getInfo().setParameterIdxNames(PARAM_KEY_TYPE, PARAM_KEY_TYPE_NAME);
            setParameter(PARAM_KEY_TYPE, new Poisson(mean,PARAM_KEY_TYPE_NAME.length-1) );
        }

            @Override
        public void refreshDescription() {
            setDescription( "["+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]+"]");
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
            if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
                ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).chooseSet((int)getParameter(PARAM_KEY_TYPE).lastValue());
            }
            System.out.println(getName()+"\t\t - set key type to "+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]);
            return getNext(0);
        }
    }