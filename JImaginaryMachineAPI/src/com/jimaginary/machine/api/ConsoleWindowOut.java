/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author simonkenny
 */
public class ConsoleWindowOut {
    private static ConsoleWindowOut INSTANCE = new ConsoleWindowOut();
    private InputOutput io;
    
    protected ConsoleWindowOut() {
        // thwart instantiation
    }
    
    public static ConsoleWindowOut getInstance() {
        return INSTANCE;
    }
    
    // -----
    
    public InputOutput getIO() {
        return io;
    }
    
    public boolean ioExists() {
        return io != null;
    }
    
    public void createIO(String name) {
        io = IOProvider.getDefault().getIO(name, true);
    }
    
    public void freeIO() {
        io = null;
    }
    
    public void println(String msg) {
        if( io != null ) {
            io.getOut().println(msg);
        } else {
            System.out.println(msg);
        }
    }
    
    public void print(String msg) {
        if( io != null ) {
            io.getOut().print(msg);
        } else {
            System.out.print(msg);
        }
    }
}
