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

public class MidiPhraseChooseModifyNode extends GraphNode {
    private final int PARAM_PHRASE = 0;

    MidiPhraseChooseModifyNode() {
        super("Midi Phrase",MODIFY,1,1);
        setParameter(PARAM_PHRASE, new Uniform(MidiModalConstants.DEFAULT_NUM_OUPTUT_PHRASES)); //default
    }

    @Override
    public void refreshDescription() {
        setDescription("Phrase: ["+(int)(getParameter(PARAM_PHRASE).lastValue()+1)+"]");
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
        if( graphPacket.outputSetCollection instanceof MidiPhraseOutputSetCollection ) {
            ((MidiPhraseOutputSetCollection)graphPacket.outputSetCollection)
                    .chooseSet((int)getParameter(PARAM_PHRASE).lastValue());
        }

        System.out.println(getName()+"\t\t - choose phrase "+(int)(getParameter(PARAM_PHRASE).lastValue()+1));

        return getNext(0);
    }
}