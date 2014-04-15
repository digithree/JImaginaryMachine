/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.ConsoleWindowOut;
import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.GraphNode;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileChooserBuilder;
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
    //private final SaveCookieImpl impl;
    
    public SelectionListItemNode(SelectionListItem _item) {
        super (Children.create(new SelectionListItemNodeChildFactory(_item), true), Lookups.singleton(_item));
        //super(new Children.Array());
        item = _item;
        setDisplayName(item.getName());
        //item.addPropertyChangeListener(WeakListeners.propertyChange(this, item));
        //impl = new SaveCookieImpl();
    }
    
    public SelectionListItemNode() {
        super (Children.create(new SelectionListItemNodeChildFactory(), true));
        item = null;
        setDisplayName ("Graph Building");
        //impl = new SaveCookieImpl();
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
                    return new Action[] { new NewEmptyGraphAction(), new NewRandomGraphAction(),
                            new SaveGraphAction() };
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
            //final ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Creating graph");
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
    
    // HACK WAY OF SAVING GRAPH...
    private class SaveGraphAction extends AbstractAction {
        public SaveGraphAction () {
            putValue (NAME, "Save current graph");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("DUMMY SAVE OPERATION");
            if( GraphData.getGraph() != null ) {
                ConsoleWindowOut.getInstance().createIO("Graph save");
                String serialized = GraphData.getGraph().serialize();
                // save
                //The default dir to use if no value is stored
                File home = new File (System.getProperty("user.home"));
                //Now build a file chooser and invoke the dialog in one line of code
                //"libraries-dir" is our unique key
                File newFile = new FileChooserBuilder(Graph.class)
                        .setTitle("Save Graph")
                        .setDefaultWorkingDirectory(home)
                        .setApproveText("Save")
                        .showSaveDialog()
                        ;
                //Result will be null if the user clicked cancel or closed the dialog w/o OK
                if (newFile != null) {
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(newFile));
                        writer.write(serialized);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        try {
                            if( writer != null ) {
                                writer.close();
                                StatusDisplayer.getDefault().setStatusText("Saved Graph to file");
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } else {
                    System.out.println("new file is null");
                    StatusDisplayer.getDefault().setStatusText("Couldn't write file!");
                }
                // finish
                System.out.println(serialized);
                ConsoleWindowOut.getInstance().println(serialized);
                ConsoleWindowOut.getInstance().freeIO();
            } else {
                StatusDisplayer.getDefault().setStatusText("Couldn't save graph");
            }
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
    
    // save functionality
    /*
    public void fire(boolean modified) {
        if (modified) {
            //If the text is modified,
            //we implement SaveCookie,
            //and add the implementation to the cookieset:
            getCookieSet().assign(SaveCookie.class, impl);
        } else {
            //Otherwise, we make no assignment
            //and the SaveCookie is not made available:
            getCookieSet().assign(SaveCookie.class);
        }
    }
    */

    /*
    private class SaveCookieImpl implements SaveCookie {

        public void save() throws IOException {

            Confirmation msg = new NotifyDescriptor.Confirmation("Do you want to save Graph?",
                    NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(msg);

            //When user clicks "Yes", indicating they really want to save,
            //we need to disable the Save button and Save menu item,
            //so that it will only be usable when the next change is made
            //to the text field:
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                fire(false);
                //Implement your save functionality here.
                System.out.println("DUMMY SAVE OPERATION");
            }

        }
    }
    */
}
