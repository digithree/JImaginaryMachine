/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.MODIFY;
import com.jimaginary.machine.math.Bernoulli;
import com.jimaginary.machine.math.Poisson;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiRangeModifyNode extends GraphNode {
    private final int PARAM_OCTAVE = 0;
    private final int PARAM_OCTAVE_RND = 1;
    private final int PARAM_SIZE = 2;

    private final float PARAM_OCTAVE_PROB_MEAN = 5; // note: corresponds to octave 3 as mean
    private final String[] PARAM_OCTAVE_NAME = { "Oct -1", "Oct 0", "Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6", "Oct 7" };

    private final float PARAM_OCTAVE_PROB_RND = 0.2f;

    private final float PARAM_SIZE_PROB_MEAN = 0.5f;
    private final int PARAM_SIZE_MAX = 2;  //actually 3, including 0 because 0 is invalid size

    MidiRangeModifyNode() {
        super("Midi Range",MODIFY,3,1);
        setParameter(PARAM_OCTAVE, new Poisson(PARAM_OCTAVE_PROB_MEAN,PARAM_OCTAVE_NAME.length-1));
        setParameter(PARAM_OCTAVE_RND, new Bernoulli(PARAM_OCTAVE_PROB_RND));
        setParameter(PARAM_SIZE, new Poisson(PARAM_SIZE_PROB_MEAN,PARAM_SIZE_MAX));
    }

    @Override
    public void refreshDescription() {
        setDescription("range: ["+PARAM_OCTAVE_NAME[(int)getParameter(PARAM_OCTAVE).lastValue()]+"]"
                    +" size: ["+((int)getParameter(PARAM_SIZE).lastValue()+1)+"]");
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
            graphPacket.inputSetCollection.setRange((int)getParameter(PARAM_OCTAVE).lastValue()==0.f,
                    getParameter(PARAM_OCTAVE).lastValue()-1,getParameter(PARAM_SIZE).lastValue()+1);
        }

        System.out.println(getName()+"\t\t - set range to "+PARAM_OCTAVE_NAME[(int)getParameter(PARAM_OCTAVE).lastValue()]
                    +", size "+getParameter(PARAM_SIZE).lastValue());

        return getNext(0);
    }
}