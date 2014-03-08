/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.GraphData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "com.jimaginary.machine.graph.viewer.AddNode"
)
@ActionRegistration(
        iconBase = "com/jimaginary/machine/graph/viewer/Add-16.png",
        displayName = "#CTL_AddNode"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0),
    @ActionReference(path = "Toolbars/Edit", position = 300)
})
@Messages("CTL_AddNode=Add Node")
public final class AddNode implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphData.addNode();
    }
}
