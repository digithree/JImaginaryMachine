/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jimaginary.machine.set.selector;

import com.jimaginary.machine.api.SetData;
import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//jimaginary.machine.set.selector//SetSelector//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SetSelectorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "jimaginary.machine.set.selector.SetSelectorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SetSelectorAction",
        preferredID = "SetSelectorTopComponent"
)
@Messages({
    "CTL_SetSelectorAction=SetSelector",
    "CTL_SetSelectorTopComponent=SetSelector Window",
    "HINT_SetSelectorTopComponent=This is a SetSelector window"
})
public final class SetSelectorTopComponent extends TopComponent
        implements ExplorerManager.Provider {
    
    private final InstanceContent content = new InstanceContent();
    private final ExplorerManager mgr = new ExplorerManager();

    public SetSelectorTopComponent() {
        initComponents();
        setName(Bundle.CTL_SetSelectorTopComponent());
        setToolTipText(Bundle.HINT_SetSelectorTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        //associateLookup(new AbstractLookup(content));

        setLayout(new BorderLayout());
        add(new BeanTreeView(), BorderLayout.CENTER);

        setDisplayName("Set Selector");
        
        mgr.setRootContext(new SetItemNode());
    }
    
    public class SetDataObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            System.out.println("SetDataObserver:update()");
            mgr.setRootContext(new SetItemNode());
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        SetData.getInstance().addObserver(new SetDataObserver());
    }

    @Override
    public void componentClosed() {
        SetData.getInstance().deleteObservers();
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
    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    public InstanceContent getInstanceContent() {
        return content;
    }
}
