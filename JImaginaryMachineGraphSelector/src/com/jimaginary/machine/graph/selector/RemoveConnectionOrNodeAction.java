/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.selector;

import com.jimaginary.machine.api.GraphData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "com.jimaginary.machine.graph.selector.RemoveConnectionOrNode"
)
@ActionRegistration(
        iconBase = "com/jimaginary/machine/graph/selector/Close-16.png",
        displayName = "#CTL_RemoveConnectionOrNode"
)
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 200, separatorAfter = 250),
    @ActionReference(path = "Toolbars/Edit", position = 400)
})
@Messages("CTL_RemoveConnectionOrNode=Remove")
public final class RemoveConnectionOrNodeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphData.removeNode();
    }
}
