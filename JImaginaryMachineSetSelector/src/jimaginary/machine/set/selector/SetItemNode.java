/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jimaginary.machine.set.selector;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author simonkenny
 */
public class SetItemNode extends AbstractNode implements PropertyChangeListener {
    private final SetItem setItem;
    
    SetItemNode() {
        super (Children.create(new SetItemNodeChildFactory(), true));
        setItem = null;
        setDisplayName("Sets");
    }
    
    /*
    SetItemNode(String setName) {
        super(Children.LEAF);
        this.setItem = new SetItem(setName);
        setDisplayName(setItem.getName());
    }
    */
    
    SetItemNode(SetItem setItem) {
        super (Children.create(new SetItemNodeChildFactory(setItem), true),Lookups.singleton(setItem));
        this.setItem = setItem;
        setDisplayName(setItem.getName());
    }
    
    @Override
    public Action[] getActions (boolean popup) {
        if( setItem != null ) {
            return new Action[] { new PlayAction() };
        }
        return null;
    }
    
    private class PlayAction extends AbstractAction {
        public PlayAction () {
            putValue (NAME, "Play");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SetItem obj = getLookup().lookup(SetItem.class);
            //ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Creating graph");
            //progressHandle.switchToIndeterminate();
            //progressHandle.start();
            /*
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
                    */
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
