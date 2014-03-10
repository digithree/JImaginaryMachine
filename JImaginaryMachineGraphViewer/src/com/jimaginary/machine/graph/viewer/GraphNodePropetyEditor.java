/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.math.MathFunction;
import com.jimaginary.machine.math.MathFunctionFactory;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import org.openide.util.Exceptions;

/**
 *
 * @author simonkenny
 */
public class GraphNodePropetyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        MathFunction func = (MathFunction) getValue();
        if (func == null) {
            return "No Function Set";
        }
        return func.getName();
    }

    @Override
    public void setAsText(String s) {
        try {
            setValue( MathFunctionFactory.createByParse(s) );
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
