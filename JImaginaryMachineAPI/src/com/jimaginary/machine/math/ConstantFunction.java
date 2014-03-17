/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.math;

/**
 *
 * @author simonkenny
 */
public class ConstantFunction extends MathFunction {
    public ConstantFunction() {
        super("Constant",1);
        setParamName(0, "Value");   // no upper/lower bound
    }
    
    public ConstantFunction(float val) {
        super("Constant",1);
        setParamName(0, "Value");   // no upper/lower bound
        setParameter(0, val);
    }
    
    @Override
    public float evaluate() {
        return paramVals[0];
    }
    
    @Override
    public float lastValue() {
        return paramVals[0];
    }

    @Override
    public float probMassOrDensity(float x) {
        if( x == paramVals[0] ) {
            return 1.f;
        }
        return 0.f;
    }
}
