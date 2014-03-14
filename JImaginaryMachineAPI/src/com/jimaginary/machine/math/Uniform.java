/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.math;

/**
 * Uniform distribution random variable generator function
 * This is a discrete distribution, useful for picking
 * an array index for example with equal likelihood.
 * 
 * Parameter 0: number of choices
 * 
 * @author simonkenny
 */
public class Uniform extends MathFunction {
    public Uniform() {
        super("Uniform", 1);
        setupParameter(0,"Choices",0.f,Float.MAX_VALUE);
    }
    
    public Uniform(float val) {
        super("Uniform", 1);
        setupParameter(0,"Choices",0.f,Float.MAX_VALUE);
        setParameter(0,val);
    }

    @Override
    public float evaluate() {
        lastVal = (int)(Math.random()*paramVals[0]);
        return lastVal;
    }
}
