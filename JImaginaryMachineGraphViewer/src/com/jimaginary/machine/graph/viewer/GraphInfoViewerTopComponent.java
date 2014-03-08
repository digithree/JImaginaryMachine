/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.viewer;

import java.util.Collection;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.jimaginary.machine.graph//GraphInfoViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "GraphInfoViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.jimaginary.machine.graph.GraphInfoViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_GraphInfoViewerAction",
        preferredID = "GraphInfoViewerTopComponent"
)
@Messages({
    "CTL_GraphInfoViewerAction=GraphInfoViewer",
    "CTL_GraphInfoViewerTopComponent=GraphInfoViewer Window",
    "HINT_GraphInfoViewerTopComponent=This is a GraphInfoViewer window"
})
public final class GraphInfoViewerTopComponent extends TopComponent
        implements LookupListener {

    private Lookup.Result<GraphTypeNode> result = null;
    
    public GraphInfoViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_GraphInfoViewerTopComponent());
        setToolTipText(Bundle.HINT_GraphInfoViewerTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelGraphName = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabelGraphName, org.openide.util.NbBundle.getMessage(GraphInfoViewerTopComponent.class, "GraphInfoViewerTopComponent.jLabelGraphName.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGraphName, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGraphName)
                .addContainerGap(278, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelGraphName;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(GraphTypeNode.class);
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        Collection<? extends GraphTypeNode> allEvents = result.allInstances();
        if (!allEvents.isEmpty()) {
            GraphTypeNode node = allEvents.iterator().next();
            jLabelGraphName.setText(node.getDisplayName());
        } else {
            jLabelGraphName.setText("[no graph selection]");
        }
    }
}
