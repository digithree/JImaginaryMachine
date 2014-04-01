/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jimaginary.machine.set.selector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author simonkenny
 */
public class SetItemNode extends AbstractNode implements PropertyChangeListener {
    private final SetItem setItem;
    
    SetItemNode() {
        super (Children.create(new SetItemNodeChildFactory(), true));
        setItem = new SetItem();
        setDisplayName("Sets");
    }
    
    SetItemNode(String setName) {
        super(Children.LEAF);
        this.setItem = new SetItem(setName);
        setDisplayName(setItem.getName());
    }
    
    SetItemNode(SetItem setItem) {
        super(Children.LEAF);
        this.setItem = setItem;
        setDisplayName(setItem.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
