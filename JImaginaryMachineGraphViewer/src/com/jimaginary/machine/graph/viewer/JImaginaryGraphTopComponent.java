/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.api.GraphNode;
import com.jimaginary.machine.api.GraphNodeInfo;
import com.jimaginary.machine.math.Matrix;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.vmd.VMDColorScheme;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
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
            implements LookupListener, ExplorerManager.Provider {

    private final transient ExplorerManager explorerManager = new ExplorerManager();
    //private InstanceContent instanceContent = new InstanceContent();
    
    //private VMDGraphScene scene;
    private VMDGraphScene scene;
    private final VMDColorScheme colorScheme;
    //private LayerWidget layer;
    private LayerWidget connectionLayer;
    private final List<GraphNodeInfo> graphNodeInfos;
    
    //private final DummySaveNode dummySaveNode;

    public JImaginaryGraphTopComponent() {
        initComponents();
        setName(Bundle.CTL_JImaginaryGraphTopComponent());
        setToolTipText(Bundle.HINT_JImaginaryGraphTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        //associateLookup(new AbstractLookup(instanceContent));
        
        graphNodeInfos = new ArrayList<GraphNodeInfo>();
        
        // create initial scene
        colorScheme = new VMDImaginaryMachineColorScheme();
        scene = new VMDGraphScene(colorScheme);
        jScrollPane.setViewportView(scene.createView());
        createScene();
        
        if( GraphData.getGraph() != null ) {
            updateScene(GraphData.getGraph());
        }        
        GraphData.addObserver(new GraphObserver());
        
        //setActivatedNodes(new Node[]{dummySaveNode = new DummySaveNode()});
        //dummySaveNode.fire(true);
    }
    
    private GraphNodeInfo createWidgetAndNode(GraphNodeInfo info, Point loc) {
        String name = info.getName();
        VMDNodeWidget node = (VMDNodeWidget)scene.addNode(name);
        node.setNodeName(name);
        if( loc != null ) {
            node.setPreferredLocation(loc);
        }
        createPinsForWidget(info);
        graphNodeInfos.add(info);
        addWidgetAction(node,info);
        System.out.println("Added node: "+name);
        scene.validate();
        return info;
    }
    
    private void clearScene() {
        scene = new VMDGraphScene(colorScheme);
        jScrollPane.setViewportView(scene.createView());
        createScene();
        connectionLayer = new LayerWidget(scene);
        scene.addChild(connectionLayer);        
        scene.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);
        scene.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_PARENTS);
        graphNodeInfos.clear();
    }
    
    private void createPinsForWidget(GraphNodeInfo info) {
        int id = info.getId();
        System.out.println("createPinsForWidget: "+info.getName());
        VMDPinWidget widget = (VMDPinWidget)scene.addPin(info.getName(),id+"-pinIn");
        widget.setPinName(id+"-pinIn");
        for( int i = 0 ; i < info.getNumConnections(); i++ ) {
            widget = (VMDPinWidget)scene.addPin(info.getName(),id+"-pinOut-"+(i+1));
            widget.setPinName(id+"-pinOut-"+(i+1));
            widget.getActions().addAction(ActionFactory.createExtendedConnectAction(connectionLayer, new PinConnectProvider()));
        }
    }
    
    private boolean addEdge(String pinFrom, String pinTo) {
        if( !pinFrom.contains("pinOut") || !pinTo.contains("pinIn") ) {
            return false;
        }
        String edge = "edge-" + edgeCount++;
        System.out.println("Trying to add edge ("+edge+") from "+pinFrom+" to "+pinTo);
        ConnectionWidget widget = (ConnectionWidget)scene.addEdge(edge);
        // TODO : set ConnectionWidget colour
        scene.setEdgeSource(edge,pinFrom);
        scene.setEdgeTarget(edge,pinTo);
        // add key action to edges
        //widget.getActions().addAction(new KeyboardDeleteActionWidget());
        return true;
    }
    
    private void addWidgetAction(VMDNodeWidget widget, final GraphNodeInfo info) {
        //widget.getActions().addAction(ActionFactory.createExtendedConnectAction(connectionLayer, new MyConnectProvider()));
        //widget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new MyConnectProvider()));
        widget.getActions().addAction(ActionFactory.createMoveAction());
        widget.getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {
            @Override
            public boolean isAimingAllowed(Widget widget, Point point, boolean bln) {
                return true;
            }

            @Override
            public boolean isSelectionAllowed(Widget widget, Point point, boolean bln) {
                return true;
            }

            @Override
            public void select(Widget widget, Point point, boolean bln) {
                AbstractNode node = new AbstractNode(Children.LEAF) {
                    @Override
                    protected Sheet createSheet() {
                        Sheet sheet = super.createSheet();
                        Sheet.Set set = Sheet.createPropertiesSet();
                        System.out.println("creating properties sheet with "+info.getNumParameters()+" properties");
                        for( int i = 0 ; i < info.getNumParameters() ; i++ ) {
                            /*
                            ParameterProperty prop = new ParameterProperty(info,i);
                            prop.setDisplayName(info.getParameterName(i));
                            prop.setName(info.getParameterName(i));
                            set.put(prop);
                            System.out.println("put property "+i);
                            */
                            try {
                                PropertySupport.Reflection prop = new PropertySupport.Reflection(new PropertyWrapper(info), String.class, "parameter"+(i+1));
                                prop.setPropertyEditorClass(ParameterPropertyEditor.class);
                                prop.setName(info.getParameterName(i));
                                set.put(prop);
                            } catch (NoSuchMethodException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        sheet.put(set);
                        return sheet;
                    }
                };
                node.setDisplayName(info.getName());
                node.setShortDescription("Description of " + info.getName());
                explorerManager.setRootContext(node);
                try {
                    explorerManager.setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }));
        //widget.getActions().addAction(new KeyboardDeleteActionWidget());
        /*
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            @Override
            public JPopupMenu getPopupMenu(final Widget widget, Point localLocation) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem propsMenu = new JMenuItem("Properties");
                propsMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AbstractNode node = new AbstractNode(Children.LEAF) {
                            @Override
                            protected Sheet createSheet() {
                                Sheet sheet = super.createSheet();
                                Sheet.Set set = Sheet.createPropertiesSet();
                                set.put(new ParameterProperty(item));
                                sheet.put(set);
                                return sheet;
                            }
                        };
                        node.setDisplayName(item.getDisplayName());
                        node.setShortDescription("Description of " + item.getDisplayName());
                        NodeOperation.getDefault().showProperties(node);
                    }
                });
                popup.add(propsMenu);
                return popup;
            }
        }));
        */
    }
    
    public class PropertyWrapper {
        private final GraphNodeInfo info;
        
        public PropertyWrapper( GraphNodeInfo info ) {
            this.info = info;
        }
        
        private void update() {
            GraphNode node = GraphData.getGraph().getNodeById(info.getId());
            if( node != null ) {
                try {
                    node.setInfo(info);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        
        public String getParameter1() {
            return info.getParameter(0);
        }
        
        public String getParameter2() {
            return info.getParameter(1);
        }
        
        public String getParameter3() {
            return info.getParameter(2);
        }
        
        public String getParameter4() {
            return info.getParameter(3);
        }
        
        public String getParameter5() {
            return info.getParameter(4);
        }
        
        public void setParameter1(String param) {
            info.setParameter(0, param);
            update();
        }
        
        public void setParameter2(String param) {
            info.setParameter(1, param);
            update();
        }
        
        public void setParameter3(String param) {
            info.setParameter(2, param);
            update();
        }
        
        public void setParameter4(String param) {
            info.setParameter(3, param);
            update();
        }
        
        public void setParameter5(String param) {
            info.setParameter(4, param);
            update();
        }
    }
    
    private void createScene() {
        scene.getActions().addAction(new KeyboardDeleteActionWidget());
        // click on graphWindow action
        scene.getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {
            @Override
            public boolean isAimingAllowed(Widget widget, Point point, boolean bln) {
                return true;
            }

            @Override
            public boolean isSelectionAllowed(Widget widget, Point point, boolean bln) {
                return true;
            }

            @Override
            public void select(Widget sentWidget, Point loc, boolean bln) {
                if( GraphData.getGraph() == null ) {
                    //JOptionPane.showMessageDialog(null, "Couldn't add node, no graph set");
                    StatusDisplayer.getDefault().setStatusText("Couldn't add node, no graph set");
                    return;
                }
                if( GraphData.getNodeName() == null ) {
                    //JOptionPane.showMessageDialog(null, "Couldn't add node, no node type selected");
                    StatusDisplayer.getDefault().setStatusText("Couldn't add node, no node type selected");
                    return;
                }
                NotifyDescriptor d = new NotifyDescriptor
                        .Confirmation("Add "+GraphData.getNodeName()+" ?"
                            , "Dialog Title", NotifyDescriptor.OK_CANCEL_OPTION);
                if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                    // get node
                    // TODO : is this too strongly coupled, should not need to
                    //          know how to make nodes
                    GraphNodeInfo info = GraphData.getGraph().addNodeByName(GraphData.getNodeName());
                    if( info != null ) {
                        createWidgetAndNode(info,loc);
                        // fires message to listening nodes of any changes
                        //GraphData.getGraph().finishChanges();
                        StatusDisplayer.getDefault().setStatusText("Added node: "+info.getName());
                    } else {
                        //JOptionPane.showMessageDialog(null, "Couldn't add node: "+GraphData.getNodeName());
                        StatusDisplayer.getDefault().setStatusText("Couldn't add node: "+GraphData.getNodeName());
                    }
                }
            }

        }));
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
    
    @Override
    public void componentActivated() {
        scene.getView().requestFocusInWindow();
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
        // do nothing?
    }
    
    private int edgeCount = 0;
    
    public void updateScene(Graph graph) {
        System.out.println("updateScene called");
        
        // get rid of all widgets
        clearScene();
        // create graph
        Matrix adjMatrix = graph.getAdjacencyMatrix();
        // add nodes
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            System.out.println("Adding node "+i);
            createWidgetAndNode(graph.getNodeById(i).getInfo(), null);
            // note: automatically adds pins
        }
        // add edges
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            for( int j = 0 ; j < adjMatrix.getSizeX() ; j++ ) {
                if( adjMatrix.get(i,j) > 0 ) {
                    addEdge(i+"-pinOut-"+(int)adjMatrix.get(i,j),j+"-pinIn");
                }
            }
        }
        // layout scene (uses force-directed layout)
        scene.layoutScene();
        
        //setActivatedNodes(new Node[]{dummySaveNode});
        //dummySaveNode.fire(true);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    
    public class GraphObserver implements Observer {
        @Override
        public void update(Observable obj, Object arg) {
            updateScene((Graph)obj);
        }
    }
    
    private class PinConnectProvider implements ConnectProvider {
        @Override
        public boolean isSourceWidget(Widget source) {
            //System.out.println("PinConnectProvider isSourceWidget() called");
            return source instanceof VMDPinWidget && source != null;
        }

        @Override
        public ConnectorState isTargetWidget(Widget src, Widget trg) {
            //System.out.println("PinConnectProvider isTargetWidget() called");
            return src != trg && trg instanceof VMDPinWidget ? ConnectorState.ACCEPT : ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomTargetWidgetResolver(Scene arg0) {
            return false;
        }

        @Override
        public Widget resolveTargetWidget(Scene arg0, Point arg1) {
            return null;
        }

        @Override
        public void createConnection(Widget source, Widget target) {
            String sourcePin = (String)scene.findObject(source);
            String targetPin = (String)scene.findObject(target);
            if( sourcePin == null || targetPin == null ) {
                StatusDisplayer.getDefault().setStatusText("connection nodes: couldn't find pin object from widget");
                return;
            }
            int sourceId = Integer.parseInt(sourcePin.split("[-]")[0]);
            int targetId = Integer.parseInt(targetPin.split("[-]")[0]);
            System.out.println("Trying to connect nodes "+sourcePin+" to "+targetPin);
            GraphNode sourceNode = GraphData.getGraph().getNodeById(sourceId);
            GraphNode targetNode = GraphData.getGraph().getNodeById(targetId);
            if( sourceNode == null || targetNode == null ) {
                System.out.println("connection nodes: couldn't find nodes to connect in data model!");
                StatusDisplayer.getDefault().setStatusText("connection nodes: couldn't find nodes to connect in data model!");
                return;
            } // else go for it
            boolean result = sourceNode.addNext(targetNode);
            if( !result ) {
                System.out.println("connection nodes: couldn't connect nodes in graph data model!");
                StatusDisplayer.getDefault().setStatusText("connection nodes: couldn't connect nodes in graph data model!");
                return;
            }
            result = addEdge(((VMDPinWidget)source).getPinName(),
                    ((VMDPinWidget)target).getPinName() );
            if( result ) {
                StatusDisplayer.getDefault().setStatusText("connection nodes: Pins connected");
                System.out.println("pins connected: "+((VMDPinWidget)source).getPinName()
                        + " to "+ ((VMDPinWidget)target).getPinName());
            } else {
                //JOptionPane.showMessageDialog(null, "Couldn't connect pins");
                StatusDisplayer.getDefault().setStatusText("connection nodes: Couldn't connect pins (visual)");
            }
        }
    }
    
    private final class KeyboardDeleteActionWidget extends WidgetAction.Adapter {
        @Override
        public WidgetAction.State keyPressed(Widget widget, WidgetAction.WidgetKeyEvent event) {
            System.out.println("--- key pressed, captured by scene --- ");
            if (event.getKeyCode() == KeyEvent.VK_DELETE || event.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
                System.out.println("is delete key");
                //widget.removeFromParent();
                Set selected = scene.getSelectedObjects();
                //List<Node> selectedNodes = new ArrayList();
                System.out.println("nodes selected = "+selected.size());
                for (Object obj : selected) {
                   if (obj instanceof String){
                       String str = (String)obj;
                       System.out.println("String found "+str+" is selected");
                       if( str.contains("pin") ) {
                           //JOptionPane.showMessageDialog(null, "Can't delete pin "+str+" directly, delete node instead");
                           StatusDisplayer.getDefault().setStatusText("Can't delete pin "+str+" directly, delete node instead");
                           return State.CONSUMED;
                       }
                       NotifyDescriptor d = new NotifyDescriptor
                                .Confirmation("Delete "+str+" ?"
                                    , "Dialog Title", NotifyDescriptor.OK_CANCEL_OPTION);
                       if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                            if( !str.contains("edge") ) {
                                // then is node
                                System.out.println("** removing, is node");
                                if( GraphData.getGraph().removeNodeById(Integer.parseInt(str.split("[-]")[1])) ) {
                                    scene.removeNodeWithEdges(str);    
                                    StatusDisplayer.getDefault().setStatusText("removed node: "+str);
                                } else {
                                    StatusDisplayer.getDefault().setStatusText("couldn't remove node: "+str);
                                }
                            } else {
                                // is edge
                                System.out.println("** removing, is edge");
                                String sourcePin = scene.getEdgeSource(str);
                                String targetPin = scene.getEdgeTarget(str);
                                int sourceId = Integer.parseInt(sourcePin.split("[-]")[0]);
                                int targetId = Integer.parseInt(targetPin.split("[-]")[0]);
                                GraphNode sourceNode = GraphData.getGraph().getNodeById(sourceId);
                                GraphNode targetNode = GraphData.getGraph().getNodeById(targetId);
                                if( sourceNode == null || targetNode == null ) {
                                    //JOptionPane.showMessageDialog(null, "couldn't find edge in data model!");
                                    StatusDisplayer.getDefault().setStatusText("couldn't find edge in data model!");
                                    return State.CONSUMED;
                                }
                                // else, remove
                                if( !sourceNode.removeNextConnection(targetNode) ) {
                                    //JOptionPane.showMessageDialog(null, "problem removing node edge from underlying data model!");
                                    StatusDisplayer.getDefault().setStatusText("problem removing node edge from underlying data model!");
                                }
                                scene.removeEdge(str);
                            }
                            GraphData.getGraph().finishChanges();
                       }
                   }
                }
                // remove?
            }
            return State.CONSUMED;
        }
    }
}
