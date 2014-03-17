/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.GraphNode;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author simonkenny
 */
public class SelectionListItemNode extends AbstractNode implements PropertyChangeListener {
    private final SelectionListItem item;
    
    public SelectionListItemNode(SelectionListItem _item) {
        super (Children.create(new SelectionListItemNodeChildFactory(_item), true), Lookups.singleton(_item));
        //super(new Children.Array());
        item = _item;
        setDisplayName(item.getName());
        //item.addPropertyChangeListener(WeakListeners.propertyChange(this, item));
    }
    
    public SelectionListItemNode() {
        super (Children.create(new SelectionListItemNodeChildFactory(), true));
        item = null;
        setDisplayName ("Graph Building");
    }
    
    public String getGraphNodeName() {
        if( item != null ) {
            return item.getName();
        }
        return null;
    }
    
    public String getDescription() {
        return item.getDescription();
    }
    
    @Override
    public Image getIcon (int type) {
        if( item != null ) {
            if( item.getType() == SelectionListItem.GRAPH ) {
                return ImageUtilities.loadImage ("resources/graphicon.png");
            } else {
                if( item.getType() == GraphNode.SAMPLE ) {
                    return ImageUtilities.loadImage ("resources/inArrow.png");
                } else if( item.getType() == GraphNode.WRITE ) {
                    return ImageUtilities.loadImage ("resources/outArrow.png");
                } // else don't set yet : TODO : get proper icons
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
        if( item != null ) {
            switch(item.getType()) {
                case SelectionListItem.GRAPH:
                    return new Action[] { new NewEmptyGraphAction(), new NewRandomGraphAction() };
                // other cases?
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
            SelectionListItem obj = getLookup().lookup(SelectionListItem.class);
            //ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Creating graph");
            //progressHandle.switchToIndeterminate();
            //progressHandle.start();
            boolean result = GraphData.setGraph(new Graph(obj.getResPackName()));
            //progressHandle.finish();
            if( result ) {
                if( GraphData.getGraph().isValid() ) {
                    GraphData.getGraph().finishChanges();
                    //JOptionPane.showMessageDialog(null, "Created new empty " + obj);
                    StatusDisplayer.getDefault().setStatusText("Created new empty " + obj);
                    return;
                }
            } // else if failed
            StatusDisplayer.getDefault().setStatusText("Couldn't create new empty " + obj);
        }
    }
    
    private class NewRandomGraphAction extends AbstractAction {
        public NewRandomGraphAction () {
            putValue (NAME, "Create random graph");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SelectionListItem obj = getLookup().lookup(SelectionListItem.class);
            final ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Creating graph");
            /*
            Thread progressThread = new Thread( new Runnable() 
            {
                public void run() 
                {
                    progressHandle.switchToIndeterminate();
                    progressHandle.start();
                }
            });//.start();
            progressThread.start();
                    */
            boolean result = GraphData.setGraph(new Graph(obj.getResPackName()));
            /*
            try {
                progressThread.join();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            progressHandle.finish();
                    */
            if( result ) {
                if( GraphData.getGraph().isValid() ) {
                    GraphData.getGraph().generateRandom();
                    //JOptionPane.showMessageDialog(null, "Created new random " + obj);
                    StatusDisplayer.getDefault().setStatusText("Created new random " + obj);
                }
            }
            StatusDisplayer.getDefault().setStatusText("Couldn't create new random " + obj);
        }
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        SelectionListItem obj = getLookup().lookup(SelectionListItem.class);

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
