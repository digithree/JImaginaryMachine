/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.Matrix;
import java.util.Observable;
import java.util.Observer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.jimaginary.machine.graph//JImaginaryGraph//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "JImaginaryGraphTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.jimaginary.machine.graph.JImaginaryGraphTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_JImaginaryGraphAction",
        preferredID = "JImaginaryGraphTopComponent"
)
@Messages({
    "CTL_JImaginaryGraphAction=JImaginaryGraph",
    "CTL_JImaginaryGraphTopComponent=JImaginaryGraph Window",
    "HINT_JImaginaryGraphTopComponent=This is a JImaginaryGraph window"
})
public final class JImaginaryGraphTopComponent extends TopComponent 
            implements LookupListener {

    public JImaginaryGraphTopComponent() {
        initComponents();
        setName(Bundle.CTL_JImaginaryGraphTopComponent());
        setToolTipText(Bundle.HINT_JImaginaryGraphTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        
        if( GraphData.getGraph() != null ) {
            createLayout(GraphData.getGraph());
        }        
        GraphData.addObserver(new GraphObserver());
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane = new javax.swing.JScrollPane();

        jScrollPane.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }
    
    private int edgeCount = 0;
    
    public void createLayout(Graph graph) {
        VMDGraphScene scene = new VMDGraphScene();
        jScrollPane.setViewportView(scene.createView());
        //JImaginaryGraphPinScene graphScene = new JImaginaryGraphPinScene();
        //jScrollPane.setViewportView(graphScene.createView());
        // create graph
        Matrix adjMatrix = graph.getAdjacencyMatrix();
        // add nodes
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            VMDNodeWidget widget = (VMDNodeWidget)scene.addNode(""+i);
            widget.setNodeName(graph.getNodeById(i).getName());
        }
        // add pins
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            VMDPinWidget widget = (VMDPinWidget)scene.addPin(""+i,i+"-pinIn");
            widget.setPinName("In");
            widget = (VMDPinWidget)scene.addPin(""+i,i+"-pinOut");
            widget.setPinName("Out");
        }
        // add edges
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            for( int j = 0 ; j < adjMatrix.getSizeX() ; j++ ) {
                if( adjMatrix.get(i,j) == 1 ) {
                    String edge = "edge-" + edgeCount++;
                    scene.addEdge(edge);
                    scene.setEdgeSource(edge,i+"-pinOut");
                    scene.setEdgeTarget(edge,j+"-pinIn");
                }
            }
        }
        GridGraphLayout<String,String> layout = new GridGraphLayout<String,String>();
        //layout.setChecker(true);
        //layout.setGaps(60, 60);
        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(scene,layout);
        sceneLayout.invokeLayout();
    }
    
    public class GraphObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            createLayout((Graph)o);
        }
        
    }
    
    public class VisualGraphNode implements Comparable<VisualGraphNode>{
        public int id;
        public String name;
        public String inPin;
        public String outPin;
        
        public VisualGraphNode(String _name, int _id) {
            name = _name;
            id = _id;
        }

        @Override
        public int compareTo(VisualGraphNode otherNode) {
            return id - otherNode.id;
        }
    }
    
    /*
    
    
    public class JImaginaryGraphPinScene extends GraphPinScene<VisualGraphNode,String,String> {
        private final LayerWidget mainLayer;
        private final LayerWidget connectionLayer;
        
        JImaginaryGraphPinScene() {
            mainLayer = new LayerWidget(this);
            addChild(mainLayer);
            connectionLayer = new LayerWidget(this);
            addChild(connectionLayer);
        }
        
        @Override
        protected Widget attachNodeWidget(VisualGraphNode node) {
                IconNodeWidget widget = new IconNodeWidget(this, IconNodeWidget.TextOrientation.RIGHT_CENTER);
                widget.setImage(
                    ImageUtilities.loadImage("resources/blockdevice-3.png")
                );
                widget.setLabel(node.name);
                
                //widget.getLabelWidget().setLayout(
                //        LayoutFactory.createHorizontalFlowLayout(
                //                LayoutFactory.SerialAlignment.JUSTIFY, 40
                //        )
                //);
                widget.getImageWidget().setLayout(
                        LayoutFactory.createHorizontalFlowLayout(
                                LayoutFactory.SerialAlignment.RIGHT_BOTTOM, 40
                        )
                );
                widget.getActions().addAction(ActionFactory.createMoveAction());
                mainLayer.addChild(widget);
                return widget;
        }

        @Override
        protected Widget attachEdgeWidget(String edge) {
            ConnectionWidget widget = new ConnectionWidget(this);
            widget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
            widget.setRouter(
                    RouterFactory.createOrthogonalSearchRouter(mainLayer,connectionLayer)
            );
            connectionLayer.addChild(widget);
            return widget;
        }

        @Override
        protected Widget attachPinWidget(VisualGraphNode node, String pin) {
            ImageWidget widget = new ImageWidget(this,
                    ImageUtilities.loadImage("resources/pinsmall.png")
            );
            IconNodeWidget n = (IconNodeWidget)findWidget(node);
            //n.getLabelWidget().addChild(widget);
            n.getImageWidget().addChild(widget);
            return widget;
        }

        @Override
        protected void attachEdgeSourceAnchor(String edge, String oldPin, String pin) {
            ConnectionWidget c = (ConnectionWidget)findWidget(edge);
            Widget widget = findWidget(pin);
            Anchor a = AnchorFactory.createRectangularAnchor(widget);
            c.setSourceAnchor(a);
        }

        @Override
        protected void attachEdgeTargetAnchor(String edge, String oldPin, String pin) {
            ConnectionWidget c = (ConnectionWidget)findWidget(edge);
            Widget widget = findWidget(pin);
            Anchor a = AnchorFactory.createRectangularAnchor(widget);
            c.setTargetAnchor(a);
        }
        
    }
*/
}
