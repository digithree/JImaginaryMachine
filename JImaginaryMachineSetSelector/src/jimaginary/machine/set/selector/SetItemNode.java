/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jimaginary.machine.set.selector;

import com.digithree.codecs.midi.MidiPlayer;
import com.jimaginary.machine.api.Set;
import com.jimaginary.machine.api.SetData;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.StatusDisplayer;
import org.openide.loaders.SaveAsCapable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
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
        /*
        InstanceContent ic = Lookup.getDefault().lookup(InstanceContent.class);
        if( ic != null ) {
            ic.add(setItem);
        }
        */
        getCookieSet().assign(SaveAsCapable.class, setItem);
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
            return new Action[] { new PlayAction(), new DeleteAction() };
        }
        return null;
    }
    
    private class PlayAction extends AbstractAction {
        MidiPlayer player;
        
        public PlayAction () {
            putValue (NAME, "Play");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SetItem obj = getLookup().lookup(SetItem.class);
            Set set = SetData.getInstance().getSetByName(obj.getName());
            if( set != null ) {
                //System.out.println("Playing set: "+set.getName());
                StatusDisplayer.getDefault().setStatusText("Playing set: "+set.getName());
                player = new MidiPlayer(set.getLen());
                player.addTrack(set);
                player.setLooping(false);
                player.play();
                /*
                new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(10 * 1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(
                                "child thread  " + new Date(System.currentTimeMillis()));
                        }
                    }).start();
                */
                //while( player.isPlaying() ) {}
                //player.stop();
                //System.out.println("Finished playing set");
                //StatusDisplayer.getDefault().setStatusText("Finished playing set");
            } else {
                //System.out.println("Couldn't play, couldn't get data for set: "+obj.getName());
                StatusDisplayer.getDefault().setStatusText("Couldn't play, couldn't get data for set: "+obj.getName());
            }
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
    
    private class ExportAction extends AbstractAction {
        MidiPlayer player;
        
        public ExportAction () {
            putValue (NAME, "Export...");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO : bring up export dialog
            //SetItem obj = getLookup().lookup(SetItem.class);
        }
    }
    
    private class DeleteAction extends AbstractAction {
        MidiPlayer player;
        
        public DeleteAction () {
            putValue (NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SetItem obj = getLookup().lookup(SetItem.class);
            if( SetData.getInstance().removeSetByName(obj.getName()) ) {
                StatusDisplayer.getDefault().setStatusText("Deleted set "+obj.getName());
            } else {
                StatusDisplayer.getDefault().setStatusText("Couldn't delete set "+obj.getName());
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
