/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase.node;

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.math.Uniform;
import com.jimaginary.machine.midi.phrase.MidiModalConstants;
import com.jimaginary.machine.midi.phrase.MidiPhraseInputSetCollection;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiKeyModifyNode extends GraphNode {
    private final int PARAM_KEY = 0;    

    public MidiKeyModifyNode() {
        super("MidiKeyModifyNode",MODIFY,1,1);
        getInfo().setParameterName(PARAM_KEY, "Key");
        getInfo().setParameterNumIdx(PARAM_KEY, MidiModalConstants.NUM_KEYS);
        getInfo().setParameterIdxNames(PARAM_KEY, MidiModalConstants.KEY_NAMES);
        setParameter(PARAM_KEY, new Uniform(MidiModalConstants.NUM_KEYS));
    }

    @Override
    public void refreshDescription() {
        setDescription("key: ["+MidiModalConstants.KEY_NAMES[(int)getParameter(PARAM_KEY).lastValue()]+"]");
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
            ((MidiPhraseInputSetCollection)graphPacket.inputSetCollection).createKeyAndModeSet((int)getParameter(PARAM_KEY).lastValue(),-1);
        }

        ConsoleWindowOut.getInstance().println(getName()+"\t\t\t - set key to "+MidiModalConstants.KEY_NAMES[(int)getParameter(PARAM_KEY).lastValue()]);

        return getNext(0);
    }
}