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
public final class Matrix {
    double []elements = null;
    int sizeX, sizeY;

    public Matrix() {
            sizeX = 0;
            sizeY = 0;
    }

    public Matrix( int y, int x ) {
            sizeX = x;
            sizeY = y;
            clear();
    }

    public int getSizeX() {
        return sizeX;
    }
    
    public int getSizeY() {
        return sizeY;
    }

    // setup
    public void clear() {
            if( elements == null ) {
                    elements = new double[sizeX*sizeY];
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] = 0;
            }
    }

    // display
    public void display() {
            if( sizeX <= 0 || sizeY <= 0 ) {
                    System.out.println( "Matrix empty!" );
                    return;
            }
            System.out.println( "Size "+sizeY+" x "+sizeX);
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    if( i != 0 && i % sizeX == 0 ) {
                            System.out.print( "\n" );
                    } else if( i != 0 ) {
                            System.out.print( "\t" );
                    }
                    System.out.print( String.format("%.2f",elements[i]) );
            }
            System.out.print("\n");
    }

    // add special marker at (y,x) element
    public void display( int y, int x ) {
            if( sizeX <= 0 || sizeY <= 0 ) {
                    System.out.println( "Matrix empty!" );
                    return;
            }
            System.out.println( "Size "+sizeY+" x "+sizeX);
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    if( i != 0 && i % sizeX == 0 ) {
                            System.out.print( "\n" );
                    } else if( i != 0 ) {
                            System.out.print( "\t" );
                    }
                    System.out.print( String.format("%.2f",elements[i]) );
                    if( (i/sizeX) == y && (i%sizeX) == x ) {
                            System.out.print("*");
                    }
            }
            System.out.print("\n");
    }

    //access
    public double get( int y, int x ) {
            if( sizeX > 0 && sizeY > 0 && ((y*sizeX)+x) < (sizeX*sizeY) ) {
                    return elements[(y*sizeX)+x];
            }
            return 0;
    }

    public double getRowSum( int y ) {	
            if( sizeX <= 0 || sizeY <= 0 || y < 0 || y >= sizeY ) {
                    return 0;
            }
            double sum = 0;
            for( int i = 0 ; i < sizeX ; i++ ) {
                    sum += elements[(y*sizeX)+i];
            }
            return sum;
    }

    public boolean set( int y, int x, double val ) {
            if( sizeX > 0 && sizeY > 0 && ((y*sizeX)+x) < (sizeX*sizeY) ) {
                    elements[(y*sizeX)+x] = val;
                    return true;
            }
            return false;
    }

    public boolean set( double []newElements ) {
            if( newElements.length != (sizeX*sizeY) ) {
                    return false;
            }
            if( elements == null ) {
                    clear();
            }
            for( int i = 0 ; i < newElements.length ; i++ ) {
                    elements[i] = newElements[i];
            }
            return true;
    }

    // arithmetic
    public boolean inc( int y, int x ) {
            if( sizeX > 0 && sizeY > 0 && ((y*sizeX)+x) < (sizeX*sizeY) ) {
                    elements[(y*sizeX)+x]++;
                    return true;
            }
            return false;
    }

    public boolean add( double val ) {
            if( sizeX <= 0 || sizeY <= 0 ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] += val;
            }
            return true;
    }

    public boolean add( Matrix m ) {
            if( m.sizeX != sizeX || m.sizeY != sizeY ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] += m.elements[i];
            }
            return true;
    }

    public boolean sub( Matrix m ) {
            if( m.sizeX != sizeX || m.sizeY != sizeY ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] -= m.elements[i];
            }
            return true;
    }

    public boolean sub( double val ) {
            if( sizeX <= 0 || sizeY <= 0 ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] -= val;
            }
            return true;
    }

    public boolean mult( double val ) {
            if( sizeX <= 0 || sizeY <= 0 ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] *= val;
            }
            return true;
    }

    public boolean div( double val ) {
            if( sizeX <= 0 || sizeY <= 0 || val == 0 ) {
                    return false;
            }
            for( int i = 0 ; i < (sizeX*sizeY) ; i++ ) {
                    elements[i] /= val;
            }
            return true;
    }

    public boolean div( int y, int x, double val ) {
            if( sizeX <= 0 || sizeY <= 0 || val == 0 ) {
                    return false;
            }
            elements[(y*sizeX)+x] /= val;
            return true;
    }
}
