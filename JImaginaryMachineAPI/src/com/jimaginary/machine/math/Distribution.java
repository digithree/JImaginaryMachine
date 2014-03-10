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
public interface Distribution {
    public float[] generatePMDF(float lBound, float uBound, int numValues);
}
