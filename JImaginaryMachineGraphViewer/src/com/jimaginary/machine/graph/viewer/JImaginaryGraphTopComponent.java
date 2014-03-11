/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jimaginary.machine.graph.viewer;

import com.jimaginary.machine.api.Graph;
import com.jimaginary.machine.api.GraphData;
import com.jimaginary.machine.math.Matrix;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOperation;
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
    
    private final boolean COLLAPSE_NODES_BY_DEFAULT = true;
    
    private String currentNodeName;
    
    //private VMDGraphScene scene;
    private VMDGraphScene scene;
    //private LayerWidget layer;
    private LayerWidget connectionLayer;
    private final List<GraphNodeItem> graphNodeItems;

    public JImaginaryGraphTopComponent() {
        initComponents();
        setName(Bundle.CTL_JImaginaryGraphTopComponent());
        setToolTipText(Bundle.HINT_JImaginaryGraphTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        
        graphNodeItems = new ArrayList<GraphNodeItem>();
        
        // create initial scene
        scene = new VMDGraphScene();
        jScrollPane.setViewportView(scene.createView());
        createScene();
        
        if( GraphData.getGraph() != null ) {
            updateScene(GraphData.getGraph());
        }        
        GraphData.addObserver(new GraphObserver());
    }
    
    private GraphNodeItem createWidgetAndNode(String str, Point loc) {
        VMDNodeWidget node = (VMDNodeWidget)scene.addNode(str);
        node.setNodeName(str);
        if( loc != null ) {
            node.setPreferredLocation(loc);
        }
        createPinsForWidget(str);
        GraphNodeItem item = new GraphNodeItem(str);
        graphNodeItems.add(item);
        addWidgetAction(node,item);
        System.out.println("Added node: "+str);
        return item;
    }
    
    private void clearScene() {
        scene = new VMDGraphScene();
        jScrollPane.setViewportView(scene.createView());
        createScene();
        connectionLayer = new LayerWidget(scene);
        scene.addChild(connectionLayer);        
        scene.setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);
        graphNodeItems.clear();
    }
    
    private void createPinsForWidget(String str) {
        String parts[] = str.split("[-]");
        int id = Integer.parseInt(parts[1]);
        System.out.println("createPinsForWidget: "+str+", id: "+id);
        VMDPinWidget widget = (VMDPinWidget)scene.addPin(str,id+"-pinIn");
        //widget.setPinName("In");
        widget.setPinName(id+"-pinIn");
        widget = (VMDPinWidget)scene.addPin(str,id+"-pinOut");
        widget.setPinName("Out");
        widget.setPinName(id+"-pinOut");
        widget.getActions().addAction(ActionFactory.createExtendedConnectAction(connectionLayer, new PinConnectProvider()));
    }
    
    private boolean addEdge(String pinFrom, String pinTo) {
        if( !pinFrom.contains("pinOut") || !pinTo.contains("pinIn") ) {
            return false;
        }
        String edge = "edge-" + edgeCount++;
        System.out.println("Trying to add edge ("+edge+") from "+pinFrom+" to "+pinTo);
        scene.addEdge(edge);
        scene.setEdgeSource(edge,pinFrom);
        scene.setEdgeTarget(edge,pinTo);
        return true;
    }
    
    private void addWidgetAction(VMDNodeWidget widget, final GraphNodeItem item) {
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
                        set.put(new ParameterProperty(item));
                        sheet.put(set);
                        return sheet;
                    }
                };
                node.setDisplayName(item.getDisplayName());
                node.setShortDescription("Description of " + item.getDisplayName());
                explorerManager.setRootContext(node);
                try {
                    explorerManager.setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }));
        widget.getActions().addAction(new KeyboardDeleteActionWidget());
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
    
    private void createScene() {
        /*
        layer = new LayerWidget(scene);
        connectionLayer = new LayerWidget(scene);
        scene.addChild(layer);
        scene.addChild(connectionLayer);
        */
        // create test node
        /*
        GraphNodeWidget widget = new GraphNodeWidget(
                scene,
                new Point(100,100),
                new GraphNodeItem("SampleNode-0"));
        layer.addChild(widget);
        */
        //createWidgetAndNode("SampleNode-0");
        //scene.validate();
        
        /*
        scene.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
                JPopupMenu popup = new JPopupMenu();
                popup.add(new WidgetMenuItem(scene,"Hammer", localLocation));
                popup.add(new WidgetMenuItem(scene,"Saw", localLocation));
                return popup;
            }
        }));
        */
        
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
                        GraphNodeItem item = createWidgetAndNode(GraphData.getNodeName()+"-"+id,loc);
                        // debug test! set parameter
                        item.setParameter("Bernoulli:0.5");
                        // fires message to listening nodes of any changes
                        //GraphData.getGraph().finishChanges();
                    } else {
                        JOptionPane.showMessageDialog(null, "Couldn't add node: "+GraphData.getNodeName());
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
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
            createWidgetAndNode(graph.getNodeById(i).getName()+"-"+i, null);
            // note: automatically adds pins
        }
        // add edges
        for( int i = 0 ; i < adjMatrix.getSizeY() ; i++ ) {
            for( int j = 0 ; j < adjMatrix.getSizeX() ; j++ ) {
                if( adjMatrix.get(i,j) == 1 ) {
                    addEdge(i+"-pinOut",j+"-pinIn");
                }
            }
        }
        // layout scene (uses force-directed layout)
        scene.layoutScene();
        // add key action to edges
        System.out.println("trying to add KeyboardDeleteActionWidget action to VMDConnectionWidgets...");
        for( Widget widget : scene.getChildren() ) {
            if( widget instanceof VMDConnectionWidget ) {
                widget.getActions().addAction(new KeyboardDeleteActionWidget());
                System.out.println("added KeyboardDeleteActionWidget to connection widget");
            }
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
    
    /*
    class WidgetMenuItem extends JMenuItem {
        public WidgetMenuItem(final Scene scene, final String type, final Point loc) {
            super(type);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NotifyDescriptor.InputLine desc = 
                            new NotifyDescriptor.InputLine(
                            "Description:","Create a " + type) ;
                    DialogDisplayer.getDefault().notify(desc);
                    GraphNodeItem item = new GraphNodeItem("SomeOtherNode-3");
                    item.setParameter("wut");
                    GraphNodeWidget widget = new GraphNodeWidget(
                            scene,
                            loc,
                            item);
                }
            });
        }

    }
    */

    
    public class GraphObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            updateScene((Graph)o);
        }
        
    }
    
    public class GraphNodeWidget extends VMDNodeWidget {
        GraphNodeItem item;
        
        public GraphNodeWidget (Scene scene, Point loc, final GraphNodeItem item) {
            super(scene);
            this.item = item;
            setPreferredLocation(loc);
            setNodeName(item.getDisplayName());
            //getActions().addAction(ActionFactory.createExtendedConnectAction(connectionLayer, new MyConnectProvider()));
            getActions().addAction(ActionFactory.createMoveAction());
            getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
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
                            node.setDisplayName(item.getParameter());
                            node.setShortDescription("Description of " + item.getParameter());
                            explorerManager.setRootContext(node);
                            //NodeOperation.getDefault().showProperties(node);
                        }
                    });
                    popup.add(propsMenu);
                    return popup;
                }
            }));
        }
        
        public GraphNodeItem getItem() {
            return item;
        }
        
    }
    
    private static class ParameterProperty extends PropertySupport.ReadOnly {
        private final GraphNodeItem item;

        public ParameterProperty(GraphNodeItem _item) {
            super("graphNodeParameter", String.class, "Parameter", "Math function parameter");
            item = _item;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return item.getParameter();
        }
    }
 
    public class GraphNodeItem {
        private final int MAX_PARAMETERS = 3;
        private final int id;
        private final String graphNodeName;
        // properties
        private String []parameters;
        //private VMDNodeWidget widget;
        
        public GraphNodeItem(String displayName) { //, VMDNodeWidget widget
            //this.widget = widget;
            parameters = new String[MAX_PARAMETERS];
            String []parts = displayName.split("[-]");
            if( parts.length >= 2 ) {
                graphNodeName = parts[0];
                id = Integer.parseInt(parts[1]);
                for( int i = 0 ; i < MAX_PARAMETERS ; i++ ) {
                    if( (i+2) < parts.length ) {
                        parameters[i] = parts[i+2];
                    } else {
                        parameters[i] = "none";
                    }
                }
            } else {
                graphNodeName = "[invalid GraphNodeItem]";
                id = -1;
                for( String str : parameters ) {
                    str = "none";
                }
            }
        }
        
        public int getId() {
            return id;
        }
        
        /*
        public VMDNodeWidget getWidget() {
            return widget;
        }
        */
        
        public String getDisplayName() {
            return graphNodeName+"-"+id;
        }
        
        // TODO : repalce these with param1, 2, 3
        public String getParameter() {
            return parameters[0];
        }
        
        public void setParameter(String p) {
            parameters[0] = p;
        }

    }
  
    
    private class PinConnectProvider implements ConnectProvider {
        @Override
        public boolean isSourceWidget(Widget source) {
            System.out.println("PinConnectProvider isSourceWidget() called");
            return source instanceof VMDPinWidget && source != null;
        }

        @Override
        public ConnectorState isTargetWidget(Widget src, Widget trg) {
            System.out.println("PinConnectProvider isSourceWidget() called");
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
            boolean result = addEdge(((VMDPinWidget)source).getPinName(),
                    ((VMDPinWidget)target).getPinName() );
            /*
            if( result ) {
                JOptionPane.showMessageDialog(null, "Pins connected");
            } else {
                JOptionPane.showMessageDialog(null, "Couldn't connect pins");
            }
            */
        }

    }
    
    private final class KeyboardDeleteActionWidget extends WidgetAction.Adapter {
        @Override
        public WidgetAction.State keyPressed(Widget widget, WidgetAction.WidgetKeyEvent event) {
            if( widget instanceof ConnectionWidget ) {
                //System.out.println("Got key on widget: "+((VMDNodeWidget)widget).getNodeName());
                System.out.println("Removing connection widget");
                widget.removeFromParent();
            } else {
                System.out.println("Got key on other widget");
            }
            /*
            if (event.getKeyCode() == KeyEvent.VK_DELETE ) {
                widget.removeFromParent();
            }
                    */
            return State.CONSUMED;
        }
    }
}
