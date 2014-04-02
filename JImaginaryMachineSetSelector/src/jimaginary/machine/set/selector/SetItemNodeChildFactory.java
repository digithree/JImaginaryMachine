/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jimaginary.machine.set.selector;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.util.HelpCtx;
import com.jimaginary.machine.api.Set;
import com.jimaginary.machine.api.SetData;
import org.openide.nodes.Node;

/**
 *
 * @author simonkenny
 */
public class SetItemNodeChildFactory extends ChildFactory<SetItem>
            implements HelpCtx.Provider{
    
    private final SetItem setItem;
    
    SetItemNodeChildFactory() {
        setItem = null;
    }
    
    SetItemNodeChildFactory(SetItem setItem) {
        this.setItem = setItem;
    }

    @Override
    protected boolean createKeys(List<SetItem> listToPopulate) {
        if( setItem == null ) {
            List<Set> sets = SetData.getInstance().getSets();
            for( Set set : sets ) {
                listToPopulate.add(new SetItem(set.getName()));
            }
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(SetItem key) {
        /*
        Node result = new AbstractNode(
            Children.create(new GraphTypeNodeFactory(), true), 
            Lookups.singleton(key));
        result.setDisplayName(key.toString());
        return result;
                */
        return new SetItemNode(key);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
}
