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
public abstract class MathFunction {
    protected float paramVals[];
    protected float lastVal;
    protected float paramBoundLower[];
    protected float paramBoundUpper[];
    protected String paramNames[];
    protected String name;
    protected boolean alwaysRandomise;
    protected boolean firstEvaluation;
    
    public MathFunction(String _name, int numParameters) {
        name = _name;
        paramVals = new float[numParameters];
        paramBoundLower = new float[numParameters];
        paramBoundUpper = new float[numParameters];
        paramNames = new String[numParameters];
        // set defaults
        for( int i = 0 ; i < numParameters ; i++ ) {
            paramBoundLower[i] = -Float.MAX_VALUE;
            paramBoundUpper[i] = Float.MAX_VALUE;
        }
        alwaysRandomise = false;
        firstEvaluation = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setAlwaysRandomise(boolean state) {
        alwaysRandomise = state;
    }
    
    public boolean doAlwaysRandomise() {
        return alwaysRandomise;
    }
    
    protected boolean setParameter(int idx, float val) {
        if( paramVals != null ) {
            if( idx >= 0 && idx < paramVals.length ) {
                paramVals[idx] = val;
                return true;
            }
        }
        return false;
    }
    
    public boolean setParameters(int offset, float []vals) {
        if( paramVals != null ) {
            if( offset >= 0 && (offset+vals.length) <= paramVals.length ) {
                for( int i = 0 ; i < vals.length ; i++ ) {
                    paramVals[offset+i] = vals[i];
                }
                return true;
            }
        }
        return false;
    }
    
    protected boolean setupParameter(int idx, float lBound, float uBound) {
        if( paramVals != null ) {
            if( idx >= 0 && idx < paramVals.length ) {
                paramBoundLower[idx] = lBound;
                paramBoundUpper[idx] = uBound;
                return true;
            }
        }
        return false;
    }
    
    protected boolean setupParameter(int idx, String _name, float lBound, float uBound) {
        if( paramVals != null ) {
            if( idx >= 0 && idx < paramVals.length ) {
                paramNames[idx] = _name;
                paramBoundLower[idx] = lBound;
                paramBoundUpper[idx] = uBound;
                return true;
            }
        }
        return false;
    }
    
    public float getParameter(int idx) {
        if( paramVals != null ) {
            if( idx >= 0 && idx < paramVals.length ) {
                return paramVals[idx];
            }
        }
        return 0.f;
    }
    
    protected boolean setParamBoundLower(int idx, float val) {
        if( paramBoundLower != null ) {
            if( idx >= 0 && idx < paramBoundLower.length ) {
                paramBoundLower[idx] = val;
                return true;
            }
        }
        return false;
    }
    
    protected float getParamBoundLower(int idx) {
        if( paramBoundLower != null ) {
            if( idx >= 0 && idx < paramBoundLower.length ) {
                return paramBoundLower[idx];
            }
        }
        return 0.f;
    }
    
    protected boolean setParamBoundUpper(int idx, float val) {
        if( paramBoundUpper != null ) {
            if( idx >= 0 && idx < paramBoundUpper.length ) {
                paramBoundUpper[idx] = val;
                return true;
            }
        }
        return false;
    }
    
    protected float getParamBoundUpper(int idx) {
        if( paramBoundUpper != null ) {
            if( idx >= 0 && idx < paramBoundUpper.length ) {
                return paramBoundUpper[idx];
            }
        }
        return 0.f;
    }
    
    protected boolean setParamName(int idx, String name) {
        if( paramNames != null ) {
            if( idx >= 0 && idx < paramNames.length ) {
                paramNames[idx] = name;
                return true;
            }
        }
        return false;
    }
    
    public boolean setParamNames(int offset, String[] names) {
        if( paramVals != null ) {
            if( offset >= 0 && (offset+names.length) <= paramVals.length ) {
                for( int i = 0 ; i < names.length ; i++ ) {
                    paramNames[offset+i] = names[i];
                }
                return true;
            }
        }
        return false;
    }
    
    protected String getParamName(int idx) {
        if( paramNames != null ) {
            if( idx >= 0 && idx < paramNames.length ) {
                return paramNames[idx];
            }
        }
        return null;
    }
    
    abstract public float evaluate(float x);
    abstract public float evaluate();
    
    public float lastValue() {
        if( !firstEvaluation ) {
            firstEvaluation = true;
            return evaluate();
        }
        if( !alwaysRandomise ) {
            return lastVal;
        }
        // else
        return evaluate();
    }
}
