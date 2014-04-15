/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jimaginary.machine.set.viewer;

import com.digithree.codecs.midi.MidiCodec;
import com.digithree.codecs.midi.MidiPlayer;
import com.jimaginary.machine.api.Set;
import com.jimaginary.machine.api.SetData;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.sound.midi.MidiSystem;
import jimaginary.machine.set.selector.SetItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//jimaginary.machine.set.viewer//SetViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SetViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "jimaginary.machine.set.viewer.SetViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SetViewerAction",
        preferredID = "SetViewerTopComponent"
)
@Messages({
    "CTL_SetViewerAction=SetViewer",
    "CTL_SetViewerTopComponent=SetViewer Window",
    "HINT_SetViewerTopComponent=This is a SetViewer window"
})
public final class SetViewerTopComponent extends TopComponent 
        implements LookupListener {
    
    private InstanceContent instanceContent = new InstanceContent();
    
    private Lookup.Result<SetItem> result = null;
    ChartPanel chartPanel;

    public SetViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_SetViewerTopComponent());
        setToolTipText(Bundle.HINT_SetViewerTopComponent());
        associateLookup(new AbstractLookup(instanceContent));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelChart = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelChart;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(SetItem.class);
        result.addLookupListener (this);
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
    public void resultChanged(LookupEvent le) {
        System.out.println("SetViewerTopComponent:resultChanged(lookup)");
        Collection<? extends SetItem> allItems = result.allInstances();
        if (!allItems.isEmpty()) {
            SetItem setItem = allItems.iterator().next();
            // TODO : don't just use midi, might be wav data
            createMidiChart(SetData.getInstance().getSetByName(setItem.getName()));
            //if( getLookup().lookup(SetSavable.class) == null) {
                instanceContent.add(new SetSavable(setItem.getName()));
            //}
        }
    }
    
    private void createMidiChart(Set set) {
        if( set == null ) {
            return;
        }
        XYSeries series = new XYSeries("Distrbution");
        float setValues[] = set.getValues();
        for( int i = 0 ; i < setValues.length ; i++ ) {
            series.add(i, (int)setValues[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        // Generate the graph
        JFreeChart chart = ChartFactory.createScatterPlot(
            set.getName(),
            "Position",
            "Note",
            dataset,
            PlotOrientation.VERTICAL,  // Plot Orientation
            true,                      // Show Legend
            true,                      // Use tooltips
            false                      // Configure chart to generate URLs?
            );
        // add annotations if we have them
        XYPlot plot = chart.getXYPlot();
        for( int i = 0 ; i < setValues.length ; i++ ) {
            XYTextAnnotation an = new XYTextAnnotation(MidiCodec.noteValToString((int)setValues[i]),
                    i, (int)setValues[i]);
            plot.addAnnotation(an);
        }
        
        if( chartPanel != null ) {
            jPanelChart.remove(chartPanel);
        }
        chartPanel = new ChartPanel(chart);
        jPanelChart.setLayout(new java.awt.BorderLayout());
        jPanelChart.add(chartPanel,BorderLayout.CENTER);
        jPanelChart.validate();
    }
    
    
    //private static final Icon ICON = ImageUtilities.loadImageIcon("save.png", false);
 
    private class SetSavable extends AbstractSavable {  //implements Icon 
        private final String name;
        
        SetSavable(String name) {
            this.name = name;
            register();
        }
 
        @Override
        protected String findDisplayName() {
            return name;
        }

        @Override
        protected void handleSave() throws IOException {
            tc().instanceContent.remove(this);
            unregister();
            // do save
            System.out.println("SetItem:save for "+name);
            Set set = SetData.getInstance().getSetByName(name);

            if( set != null ) {
                /*
                FileSystem f = Repository.getDefault().getDefaultFileSystem();
                FileObject o = f.getRoot().getFileObject(name);
                */
                //The default dir to use if no value is stored
                File home = new File (System.getProperty("user.home"));
                //Now build a file chooser and invoke the dialog in one line of code
                //"libraries-dir" is our unique key
                File newFile = new FileChooserBuilder(SetItem.class)
                        .setTitle("Save "+name)
                        .setDefaultWorkingDirectory(home)
                        .setApproveText("Save")
                        .showSaveDialog()
                        ;
                //Result will be null if the user clicked cancel or closed the dialog w/o OK
                if (newFile == null) {
                    System.out.println("new file is null");
                    StatusDisplayer.getDefault().setStatusText("Couldn't write file!");
                    return;
                }

                MidiPlayer midiPlayer = new MidiPlayer(set.getLen(), false);
                midiPlayer.addTrack(set);
                System.out.println("Trying to save file "+newFile.getPath());
                try {
                    int numBytes = MidiSystem.write(midiPlayer.getSequence(), 1, newFile);
                    //MidiSystem.write(midiPlayer.getSequence(),1,file);
                    if( numBytes > 0 ) {
                        System.out.println( "Wrote "+numBytes+" bytes to "+newFile.getPath());
                        StatusDisplayer.getDefault().setStatusText("Saved file "+newFile.getPath());
                    } else {
                        System.out.println("Couldn't write file "+newFile.getPath());
                        StatusDisplayer.getDefault().setStatusText("Couldn't write file!");
                    }
                } catch( IOException e ) {
                    System.out.println( "Error writing midi sequence to file: " + e.toString() );
                    StatusDisplayer.getDefault().setStatusText("Couldn't write file!");
                }
            }
        }
 
        SetViewerTopComponent tc() {
            return SetViewerTopComponent.this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SetSavable) {
                SetSavable m = (SetSavable)obj;
                return tc() == m.tc();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return tc().hashCode();
        }

        /*
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ICON.paintIcon(c, g, x, y);
        }

        @Override
        public int getIconWidth() {
            return ICON.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return ICON.getIconHeight();
        }
        */
    }
}
