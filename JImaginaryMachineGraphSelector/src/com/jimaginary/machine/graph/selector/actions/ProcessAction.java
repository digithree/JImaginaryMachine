/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.selector.actions;

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
        id = "com.jimaginary.machine.graph.selector.actions.ProcessAction"
)
@ActionRegistration(
        iconBase = "com/jimaginary/machine/graph/selector/actions/Play-16.png",
        displayName = "#CTL_ProcessAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = -100, separatorAfter = -50),
    @ActionReference(path = "Toolbars/Edit", position = 100)
})
@Messages("CTL_ProcessAction=Process")
public final class ProcessAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphData.getGraph().process();
    }
}
