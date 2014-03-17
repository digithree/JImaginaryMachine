/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.math;

import java.text.ParseException;

/**
 *
 * @author simonkenny
 */
public class MathFunctionFactory {
    
    /**
    * string coming in must have parameters separated by colons
     * @param code
     * @return MathFunction
     * @throws java.text.ParseException
    */
    public static MathFunction createByParse(String code) throws ParseException {
        // parse expression
        String []parts = code.split("[:]");
        if( parts[0].equals("Bernoulli") ) {
            if( parts.length >= 2) {
                return new Bernoulli(Float.parseFloat(parts[1]));
            }
            return new Bernoulli();
        } else if( parts[0].equals("Constant") ) {
            if( parts.length >= 2) {
                return new ConstantFunction(Float.parseFloat(parts[1]));
            }
            return new ConstantFunction();
        } else if( parts[0].equals("Poisson")) {
            if( parts.length == 2 ) {
                return new Poisson(Float.parseFloat(parts[1]));
            } else if( parts.length == 3 ) {
                return new Poisson(Float.parseFloat(parts[1]),Float.parseFloat(parts[2]));
            }
            return new Poisson();
        } else if( parts[0].contains("ProbTable")) {
            if( parts.length < 2) {
                throw new ParseException(code,parts[0].length()+1);
            }
            ProbabilityTable probTable = new ProbabilityTable(parts.length-1);
            for( int i = 0 ; i < (parts.length-1) ; i++ ) {
                probTable.setParameter(i, Float.parseFloat(parts[i+1]));
            }
            return probTable;
            
        } else if( parts[0].equals("Uniform") ) {
            if( parts.length >= 2) {
                return new Uniform(Float.parseFloat(parts[1]));
            } //else
            return new Uniform();
        }
        return null;
    }
}
