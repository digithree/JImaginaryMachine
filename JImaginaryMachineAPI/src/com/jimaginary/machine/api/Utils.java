/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

/**
 *
 * @author simonkenny
 */
public final class Utils {
    public static float[] createUniformProbTable(int len) {
	float table[] = new float[len];
	for( int i = 0 ; i < len ; i++ ) {
            table[i] = 1.f/(float)len;
	}
	return table;
    }

    public static float[] createWeightedProbTable( float []weights ) {
        float table[] = new float[weights.length];
        float sum = 0;
        for( int i = 0 ; i < weights.length ; i++ ) {
            sum += weights[i];
        }
        for( int i = 0 ; i < weights.length ; i++ ) {
            table[i] = weights[i] / sum;
        }
        return table;
    }

    public static int getIdxFromProbTable( float []table ) {
        float rnd = (float)Math.random();
        float sum = 0.f;
        for( int i = 0 ; i < table.length ; i++ ) {
            sum += table[i];
            if( rnd < sum ) {
                return i;
            }
        }
        return 0;
    }

    private static final int NUM_ITERATIONS = 50;
    public static float getNormalRndVar( float mean, float mult ) {
        float sum = 0.f;
        for( int i = 0 ; i < NUM_ITERATIONS ; i++ ) {
            sum += (float)Math.random();
        }
        sum /= (float)NUM_ITERATIONS;
        sum -= 0.5f;
        sum *= mult;
        sum += mean;
        return sum;
    }
}
