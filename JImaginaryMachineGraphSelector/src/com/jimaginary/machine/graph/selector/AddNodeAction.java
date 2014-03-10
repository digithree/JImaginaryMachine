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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "com.jimaginary.machine.graph.selector.AddNode"
)
@ActionRegistration(
        iconBase = "com/jimaginary/machine/graph/selector/Add-16.png",
        displayName = "#CTL_AddNode"
)
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 0),
    @ActionReference(path = "Toolbars/Edit", position = 300)
})
@Messages("CTL_AddNode=Add Node")
public final class AddNodeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // this code is replicated in JImaginaryGraphTopComponent, createScene()
        if( GraphData.getGraph() == null ) {
            JOptionPane.showMessageDialog(null, "Couldn't add node, no graph set");
            return;
        }
        if( GraphData.getNodeName() == null ) {
            JOptionPane.showMessageDialog(null, "Couldn't add node, no node type selected");
            return;
        }
        NotifyDescriptor d = new NotifyDescriptor
                .Confirmation("Add "+GraphData.getNodeName()+" ?"
                    , "Dialog Title", NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            // get node
            // TODO : is this too strongly coupled, should not need to
            //          know how to make nodes
            int id = GraphData.getGraph().addNodeByName(GraphData.getNodeName());
            if( id != -1 ) {
                GraphData.getGraph().finishChanges();
            } else {
                JOptionPane.showMessageDialog(null, "Couldn't add node: "+GraphData.getNodeName());
            }
        }
    }
}
