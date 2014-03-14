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
public class Bernoulli extends MathFunction {
    private final float DEFAULT_P = 0.5f;
    
    public Bernoulli() {
        super("Bernoulli",1);
        setupParameter(0,"Success prob",0.f,1.f);
        setParameter(0,DEFAULT_P);
    }
    
    public Bernoulli(float p) {
        super("Bernoulli",1);
        setupParameter(0,"Success prob",0.f,1.f);
        setParameter(0,p);
    }

    @Override
    public float evaluate() {
        return Math.random() < paramVals[0] ? 0.f : 1.f;
    }

}
