/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.midi.phrase;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphNode;
import static com.jimaginary.machine.api.GraphNode.WRITE;
import com.jimaginary.machine.math.Poisson;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiNoteWriteNode extends GraphNode {
    final String []PARAM_JUMP_NAMES = { "None", "Next", "+2", "+3", "+4", "+5", "+6" };
    final float PARAM_JUMP_MEAN = 0.39f;

    private final int PARAM_JUMP = 0;

    MidiNoteWriteNode() {
        super("MidiNoteWriteNode",WRITE,1,1);
        setAllowsSelfConnection(true);
        getInfo().setParameterName(PARAM_JUMP, "Jump amount");
        getInfo().setParameterNumIdx(PARAM_JUMP, PARAM_JUMP_NAMES.length);
        getInfo().setParameterIdxNames(PARAM_JUMP, PARAM_JUMP_NAMES);
        setParameter(PARAM_JUMP, new Poisson(PARAM_JUMP_MEAN,PARAM_JUMP_NAMES.length-1) );
    }

    @Override
    public void refreshDescription() {
        setDescription("jump ["+PARAM_JUMP_NAMES[(int)getParameter(PARAM_JUMP).lastValue()]+"]");
    }

    // override
    @Override
    public GraphNode process( Graph.GraphPacket graphPacket ) {
        // update parameter objects (MathFunction) if info.paramsAsStr[...] changed
        try {
            updateParametersFromInfoString();
        } catch (ParseException ex) {
            Logger.getLogger(MidiKeyModifyNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        // process
        int offset = (int)getParameter(PARAM_JUMP).lastValue();

        System.out.println( getName()+"\t\t\t - process: offset "+offset );

        boolean writeSuccess = graphPacket.outputSetCollection
                .writeToSet(false,offset,graphPacket.tempSet);

        //graphPacket.displayVariables();

        if( !writeSuccess ) { // Sequence is full
            // FINISHED!
            System.out.println( "-=-=-=-=-=-==== REACHED END OF PHRASE ====-=-=-=-=-=-");
            return null;
        }

        return getNext(0);
    }
}