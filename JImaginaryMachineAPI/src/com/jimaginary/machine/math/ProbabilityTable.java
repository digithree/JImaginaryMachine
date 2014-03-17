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
public class ProbabilityTable extends MathFunction {
    public ProbabilityTable(int tableSize) {
        super("ProbTable",tableSize);
        for( int i = 0 ; i < tableSize ; i++ ) {
            setupParameter(i,"Prob-"+(i+1),0.f,1.f);
        }
    }

    @Override
    public float evaluate() {
        float rnd = (float)Math.random();
        float sum = 0.f;
        for( int i = 0 ; i < paramVals.length ; i++ ) {
            sum += paramVals[i];
            if( rnd < sum ) {
                lastVal = i;
                return lastVal;
            }
        }
        lastVal = 0;
        return 0;
    }

    @Override
    public float probMassOrDensity(float x) {
        if( (int)x >= 0 && (int)x < paramVals.length ) {
            return paramVals[(int)x];
        }
        return 0.f;
    }
}
