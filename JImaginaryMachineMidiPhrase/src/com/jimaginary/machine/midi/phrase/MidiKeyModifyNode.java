/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.MODIFY;
import com.jimaginary.machine.math.Uniform;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiKeyModifyNode extends GraphNode {
    private final int PARAM_KEY = 0;    

    MidiKeyModifyNode() {
        super("Midi Key",MODIFY,1,1);
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

        System.out.println(getName()+"\t\t\t - set key to "+MidiModalConstants.KEY_NAMES[(int)getParameter(PARAM_KEY).lastValue()]);

        return getNext(0);
    }
}