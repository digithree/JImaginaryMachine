/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase.node;

// --- Modify Nodes

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.math.ProbabilityTable;
import com.jimaginary.machine.midi.phrase.MidiPhraseInputSetCollection;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiKeyTypeModifyNode extends GraphNode {
    private final int PARAM_KEY_TYPE = 0;
    private final float[] PARAM_KEY_TYPE_PROBS = { 0.05f, 0.5f, 0.25f, 0.2f };
    private final String[] PARAM_KEY_TYPE_NAME = { "All", "Key:Any", "Key:Strong", "Key:Weak" };
    //private final float mean = 1.65f;

    public MidiKeyTypeModifyNode() {
        super("MidiKeyTypeModifyNode",MODIFY,1,1);
        getInfo().setParameterName(PARAM_KEY_TYPE, "Key type");
        getInfo().setParameterNumIdx(PARAM_KEY_TYPE, PARAM_KEY_TYPE_NAME.length);
        getInfo().setParameterIdxNames(PARAM_KEY_TYPE, PARAM_KEY_TYPE_NAME);
        //setParameter(PARAM_KEY_TYPE, new Poisson(mean,PARAM_KEY_TYPE_NAME.length) );
        setParameter(PARAM_KEY_TYPE, new ProbabilityTable(PARAM_KEY_TYPE_PROBS.length));
        getParameter(PARAM_KEY_TYPE).setParameters(0,PARAM_KEY_TYPE_PROBS);
        getParameter(PARAM_KEY_TYPE).setParamNames(0,PARAM_KEY_TYPE_NAME);
        getInfo().setParameter(PARAM_KEY_TYPE, getParameter(PARAM_KEY_TYPE).toString()); //manually set info
    }

        @Override
    public void refreshDescription() {
        setDescription( "["+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]+"]");
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
        if( graphPacket.inputSetCollection instanceof MidiPhraseInputSetCollection ) {
            ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).chooseSet((int)getParameter(PARAM_KEY_TYPE).lastValue());
        }
        ConsoleWindowOut.getInstance().println(getName()+"\t\t - set key type to "+PARAM_KEY_TYPE_NAME[(int)getParameter(PARAM_KEY_TYPE).lastValue()]);
        return getNext(0);
    }
}