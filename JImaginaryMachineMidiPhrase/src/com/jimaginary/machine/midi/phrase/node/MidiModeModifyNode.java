/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase.node;

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.math.ProbabilityTable;
import com.jimaginary.machine.midi.phrase.MidiModalConstants;
import com.jimaginary.machine.midi.phrase.MidiPhraseInputSetCollection;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiModeModifyNode extends GraphNode {
    private final int PARAM_MODE = 0;
    private final float[] PARAM_MODE_PROBS = { 0.2f, 0.2f, 0.14f, 0.1f, 0.1f, 0.2f, 0.06f };


    public MidiModeModifyNode() {
        super("MidiModeModifyNode",MODIFY,1,1);
        getInfo().setParameterName(PARAM_MODE, "Mode");
        getInfo().setParameterNumIdx(PARAM_MODE, MidiModalConstants.MODE_NAMES.length);
        getInfo().setParameterIdxNames(PARAM_MODE, MidiModalConstants.MODE_NAMES);
        setParameter(PARAM_MODE, new ProbabilityTable(PARAM_MODE_PROBS.length));
        getParameter(PARAM_MODE).setParameters(0,PARAM_MODE_PROBS);
        getParameter(PARAM_MODE).setParamNames(0,MidiModalConstants.MODE_NAMES);
        getInfo().setParameter(PARAM_MODE, getParameter(PARAM_MODE).toString()); //manually set info
    }

        @Override
    public void refreshDescription() {
        setDescription("["+MidiModalConstants.MODE_NAMES[(int)getParameter(PARAM_MODE).lastValue()]+"]");
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
            ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).createKeyAndModeSet(-1,(int)getParameter(PARAM_MODE).lastValue());
        }

        ConsoleWindowOut.getInstance().println(getName()+"\t\t\t - set mode to "+MidiModalConstants.MODE_NAMES[(int)getParameter(PARAM_MODE).lastValue()]);

        return getNext(0);
    }
}