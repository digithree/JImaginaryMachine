/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", id = "jimaginary.machine.graph.selector.OpenFileAction")
@ActionRegistration(displayName = "#CTL_OpenFileAction")
@ActionReference(path = "Menu/File", position = 10)
@Messages("CTL_OpenFileAction=Open File")
public final class OpenFileAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"user-dir" is our unique key
        File toAdd = new FileChooserBuilder("user-dir").setTitle("Open Graph")
                .setDefaultWorkingDirectory(home).setApproveText("Open").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            // open file
            Path path = FileSystems.getDefault().getPath(toAdd.getPath());
            byte[] encoded;
            try {
                encoded = Files.readAllBytes(path);
                GraphData.setGraph(Graph.deserialize(new String(encoded,StandardCharsets.UTF_8)));
                GraphData.getGraph().finishChanges();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
}
