/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.viewer;

import com.jimaginary.graph.module.reg.GraphFactory;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.GraphType;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author simonkenny
 */
public class GraphTypeNode extends AbstractNode implements PropertyChangeListener {
    private final GraphType gtp;
    
    public GraphTypeNode(GraphType _gtp) {
        super (Children.create(new GraphTypeNodeChildFactory(_gtp), true), Lookups.singleton(_gtp));
        //super(new Children.Array());
        gtp = _gtp;
        setDisplayName (gtp.getName());
        gtp.addPropertyChangeListener(WeakListeners.propertyChange(this, gtp));
    }
    
    public GraphTypeNode() {
        super (Children.create(new GraphTypeNodeChildFactory(), true));
        gtp = null;
        setDisplayName ("Graph Building");
    }
    
    @Override
    public Image getIcon (int type) {
        if( gtp != null ) {
            switch(gtp.getType()) {
                case GraphType.GRAPH:
                    return ImageUtilities.loadImage ("resources/graphicon.png");
                case GraphType.IN_NODE:
                    return ImageUtilities.loadImage ("resources/inArrow.png");
                case GraphType.OUT_NODE:
                    return ImageUtilities.loadImage ("resources/outArrow.png");
            }
        }
        return ImageUtilities.loadImage ("resources/graphicon.png");
    }
    
    @Override
    public Image getOpenedIcon(int i) {
        return getIcon (i);
    }
    
    @Override
    public Action[] getActions (boolean popup) {
        if( gtp != null ) {
            switch(gtp.getType()) {
                case GraphType.GRAPH:
                    return new Action[] { new NewEmptyGraphAction(), new NewRandomGraphAction() };
                case GraphType.IN_NODE:
                    return null;
                case GraphType.OUT_NODE:
                    return null;
            }
        }
        return null;
    } 

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("type".equals(evt.getPropertyName())) {
            this.fireDisplayNameChange(null, getDisplayName());
            System.out.println("type property change!");
        }
    }
    
    private class NewEmptyGraphAction extends AbstractAction {
        public NewEmptyGraphAction () {
            putValue (NAME, "Create empty graph");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GraphType obj = getLookup().lookup(GraphType.class);
            GraphData.setGraph(GraphFactory.createGraph(obj.getType()));
            JOptionPane.showMessageDialog(null, "Created new empty " + obj);
        }
    }
    
    private class NewRandomGraphAction extends AbstractAction {
        public NewRandomGraphAction () {
            putValue (NAME, "Create random graph");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GraphType obj = getLookup().lookup(GraphType.class);
            GraphData.setGraph(GraphFactory.createGraph(obj.getType()));
            GraphFactory.randomiseGraph(GraphData.getGraph());
            JOptionPane.showMessageDialog(null, "Created new random " + obj);
        }
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        GraphType obj = getLookup().lookup(GraphType.class);

        try {

            Property nameProp = new PropertySupport.Reflection(obj, String.class, "getName", null);
            Property typeProp = new PropertySupport.Reflection(obj, Integer.class, "typeInteger");

            nameProp.setName("name");
            typeProp.setName("type");

            set.put(nameProp);
            set.put(typeProp);

        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault();
        }

        sheet.put(set);
        return sheet;

    }
}
