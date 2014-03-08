/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author simonkenny
 */
public class GraphLookupProvider implements Lookup.Provider {

    private Lookup lookup;
    private InstanceContent instanceContent;

    public GraphLookupProvider() {
        // Create an InstanceContent to hold capabilities...
        instanceContent = new InstanceContent();
        // Create an AbstractLookup to expose the InstanceContent...
        lookup = new AbstractLookup(instanceContent);
        // Add a "Read" capability to the Lookup of the provider:
        instanceContent.add(new ReadCapability() {
            @Override
            public void read() throws Exception {
                ProgressHandle handle = ProgressHandleFactory.createHandle("Loading...");
                handle.start();
                // do something?
                // NO
                handle.finish();
            }
        });
        // Add a "Update" capability to the Lookup of the provider:
        //...to come...
        // Add a "Create" capability to the Lookup of the provider:
        //...to come...
        // Add a "Delete" capability to the Lookup of the provider:
        //...to come...
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
    
    
    public interface ReadCapability {
        public void read() throws Exception;
    }
}
