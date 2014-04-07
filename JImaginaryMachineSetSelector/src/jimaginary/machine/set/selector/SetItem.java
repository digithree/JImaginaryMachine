/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jimaginary.machine.set.selector;

import com.digithree.codecs.midi.MidiPlayer;
import com.jimaginary.machine.api.Set;
import com.jimaginary.machine.api.SetData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.midi.MidiSystem;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.SaveAsCapable;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author simonkenny
 */
public class SetItem implements SaveAsCapable {//extends AbstractSavable implements SaveAsCapable {
    private final String name;
    
    public SetItem() {
        name = "[no name]";
    }
    
    public SetItem(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    /*
    @Override
    protected String findDisplayName() {
        return name;
    }

    @Override
    protected void handleSave() throws IOException {
        InstanceContent ic = Lookup.getDefault().lookup(InstanceContent.class);
        if( ic != null ) {
            ic.remove(this);
            unregister();
            // do saving?
            System.out.println("SetItem:handleSave for "+name);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SetItem) {
            SetItem m = (SetItem)obj;
            return this == m;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    */

    @Override
    public void saveAs(FileObject folder, String filename) throws IOException {
        System.out.println("SetItem:saveAs for "+name);
        Set set = SetData.getInstance().getSetByName(name);
        
        if( set != null ) {
            FileObject newFile = folder.getFileObject(filename);

            if (newFile == null) {
                newFile = FileUtil.createData(folder, filename);
            }
            OutputStream output = newFile.getOutputStream();

            MidiPlayer midiPlayer = new MidiPlayer(set.getLen());
            midiPlayer.addTrack(set);
            try {
                int numBytes = MidiSystem.write(midiPlayer.getSequence(),0,output);
                //MidiSystem.write(midiPlayer.getSequence(),1,file);
                System.out.println( "Wrote "+numBytes+" bytes to "+newFile.getPath());
            } catch( IOException e ) {
                System.out.println( "Error writing midi sequence to file: " + e.toString() );
            }
        }
        
    }
}
