/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.GraphData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "com.jimaginary.machine.graph.selector.LinkNodes"
)
@ActionRegistration(
        iconBase = "com/jimaginary/machine/graph/selector/Link-16.png",
        displayName = "#CTL_LinkNodes"
)
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 100),
    @ActionReference(path = "Toolbars/Edit", position = 200)
})
@Messages("CTL_LinkNodes=Link Nodes")
public final class LinkNodesAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphData.linkNodes();
    }
}
