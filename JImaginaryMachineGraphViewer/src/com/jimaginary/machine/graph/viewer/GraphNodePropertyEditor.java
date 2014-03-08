/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author simonkenny
 */
public class GraphNodePropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        return "yo";
    }

    @Override
    public void setAsText(String s) {
        /*
        try {
            setValue (new SimpleDateFormat("MM/dd/yy HH:mm:ss").parse(s));
        } catch (ParseException pe) {
            IllegalArgumentException iae = new IllegalArgumentException ("Could not parse date");
            throw iae;
        }
                */
    }
}
