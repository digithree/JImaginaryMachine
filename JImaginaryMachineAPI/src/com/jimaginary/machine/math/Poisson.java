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
public class Poisson extends MathFunction {
    public Poisson() {
        super("Poisson",2);
        setupParameter(0,"Mean",0.f,Float.MAX_VALUE);
        setupParameter(1,"Max",0.f,Float.MAX_VALUE);
    }
    
    public Poisson(float mean) {
        super("Poisson",2);
        setupParameter(0,"Mean",0.f,Float.MAX_VALUE);
        setupParameter(1,"Max",0.f,Float.MAX_VALUE);
        setParameter(0,mean);
    }
    
    public Poisson(float mean, float max) {
        super("Poisson",2);
        setupParameter(0,"Mean",0.f,Float.MAX_VALUE);
        setupParameter(1,"Max",0.f,Float.MAX_VALUE);
        setParameter(0,mean);
        setParameter(1,max);
    }
    
    @Override
    public float evaluate(float x) {
        return evaluate();
    }
    
    @Override
    public float evaluate() {
        float k = 0.f;
        float p = 1.f;
        do {
            k++;
            p *= (float)Math.random();
        } while(p > (float)Math.exp(-paramVals[0]));
        lastVal = (int)((k-1) <= paramVals[1] ? (k-1) : paramVals[1]);
        return lastVal;
    }
}
