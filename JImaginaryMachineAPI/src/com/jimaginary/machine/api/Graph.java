/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import com.jimaginary.machine.generalgraph.StartNode;
import com.jimaginary.machine.math.Matrix;
import com.jimaginary.machine.settings.GraphSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author simonkenny
 */
public class Graph extends Observable {
    final int GRAPH_MAX_PROCESS_STEPS = 400;
    final float NODE_PLACEMENT_BOX_SIZE = 100.f;

    final int NODE_CONNECTION_MAX_TRIES = 30;

    final int NUM_NODES = 5;

    final float []STANDARD_NODE_SELECTION_PROBS = { 0.3f, 0.3f, 0.2f, 0.2f };
                                                                                                                    //in 	out
    final float []STANDARD_NODE_SELECTION_PROBS_MODIFY = { 0.8f, 0.2f };
    final double []MARKOV_CHAIN_NODE_SELECTION_PROBS = { 	
                                                    0.0, 0.3, 0.0, 0.25, 0.45,
                                                    0.0, 0.0, 0.65, 0.25, 0.1,
                                                    0.0, 0.3, 0.25, 0.2, 0.25,
                                                    0.0, 0.4, 0.35, 0.0, 0.25,
                                                    0.0, 0.45, 0.0, 0.35, 0.2
                                                        };
    
    GraphNode headNode;
    Matrix adjacencyMatrix;
    ArrayList<GraphNode> allNodes;
    // connection
    Matrix transitionsForBuilding;
    // gfx
    boolean shakeOut;

    // SetCollections
    private final SetCollection inputSetCollection, outputSetCollection;
    private final String graphNodeResourceName;

    // process
    GraphPacket graphPacket, lastGraphPacket;
    
    boolean valid;
    

    public Graph( String graphNodeResourceName ) {
        this.graphNodeResourceName = graphNodeResourceName;
        System.out.println("Constructing Graph, resPackName: "+graphNodeResourceName);
        SetCollection ioSets[] = GraphResourceService.getInstance().getIOSetCollections(graphNodeResourceName);
        if( ioSets != null ) {
            inputSetCollection = ioSets[0];
            outputSetCollection = ioSets[1];
            valid = true;
        } else {
            inputSetCollection = null;
            outputSetCollection = null;
            valid = true;
        }
        
        allNodes = new ArrayList<GraphNode>();
        shakeOut = false;
        transitionsForBuilding = new Matrix(NUM_NODES,NUM_NODES);
        transitionsForBuilding.set( MARKOV_CHAIN_NODE_SELECTION_PROBS );
        //graphPacket = new GraphPacket(inputSetCollection,outputSetCollection);
        lastGraphPacket = null; // why use this?
        // add StartNode to head
        headNode = (GraphNode)new StartNode();
        allNodes.add(headNode);
        addManditoryNodes();
        setChanged();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public ArrayList<GraphNode> getAllNodes() {
        return allNodes;
    }
    
    public GraphNode getNodeById(int id) {
        for( GraphNode node : allNodes ) {
            if( node.getId() == id ) {
                return node;
            }
        }
        return null;
    }

    private void addManditoryNodes() {
        System.out.println("Graph : addManditoryNodes");
        List<GraphNode> manditoryNodes = GraphResourceService.getInstance().getManditoryGraphNodes(graphNodeResourceName,true);
        for( GraphNode node : manditoryNodes ) {
            allNodes.add(node);
        }
        // connect nodes
        //   note: assume nodes only accept one connection
        for( int i = 1 ; i < allNodes.size() ; i++ ) {
            allNodes.get(i-1).addNext(0,allNodes.get(i));
        }
        // disable further connections to these nodes
        for( GraphNode node : allNodes ) {
            node.acceptsConnection = false;
        }
        System.out.println("Graph : setupManditoryNodes [end]");
    }

    public void addNode( GraphNode node ) {
        allNodes.add(node);
        //observable send notification
        setChanged();
    }
    
    // possible problem here, creaeGraphNodeByName gets from all nodes, which
    // will mean you can add nodes from an incompatable set to this graph
    //   change createGraphNodeByName to take resPackName ?
    public GraphNodeInfo addNodeByName( String nodeName ) {
        GraphNode node = GraphResourceService.getInstance().createGraphNodeByName(nodeName);
        if( node == null ) {
            return null;
        }
        // else, we got a node
        allNodes.add(node);
        setChanged();
        return node.getInfo();
    }
    
    public boolean removeNodeByName( String nodeName ) {
        for( int i = 0 ; i < allNodes.size() ; i++ ) {
            if( allNodes.get(i).getName().equals(nodeName) && allNodes.get(i).getType() != GraphNode.START ) {
                allNodes.get(i).remove();
                setChanged();
                return true;    
            }
        }
        return false;
    }
    
    public boolean removeNodeById( int id ) {
        for( int i = 0 ; i < allNodes.size() ; i++ ) {
            if( allNodes.get(i).getId() == id && allNodes.get(i).getType() != GraphNode.START ) {
                allNodes.get(i).remove();
                setChanged();
                return true;    
            }
        }
        return false;
    }
    
    public void finishChanges() {
        notifyObservers();
    }

    // reshuffle() - get rid of any gaps in id number of Nodes and sort
    private void reshuffle() {
        GraphNode.ID_COUNT = 0;
        for( GraphNode node : allNodes ) {
            node.setId(GraphNode.ID_COUNT++);
        }
        Collections.sort(allNodes);
    }

    // buildAdjacencyMatrix() - create adjacency matrix from GraphNode list
    private void buildAdjacencyMatrix() {
        adjacencyMatrix = new Matrix(GraphNode.ID_COUNT,GraphNode.ID_COUNT);
        for( int i = 0 ; i < GraphNode.ID_COUNT ; i++ ) {
            for( int j = 0 ; j < GraphNode.ID_COUNT ; j++ ) {
                int idx = allNodes.get(i).nextContainsAt(allNodes.get(j));
                adjacencyMatrix.set(i, j, idx+1);
            }
        }
        // debug
        adjacencyMatrix.display();
    }
    
    public Matrix getAdjacencyMatrix() {
        // reshuffle and rebuild every time
        //if( adjacencyMatrix == null ) {
            reshuffle();
            buildAdjacencyMatrix();
        //}
        return adjacencyMatrix;
    }

    
    public void process() {
        ConsoleWindowOut.getInstance().createIO("process");
        if( !valid ) {
            return;
        }
        lastGraphPacket = graphPacket;
        outputSetCollection.clearSetsAndReinitialise();
        graphPacket = new GraphPacket(inputSetCollection,outputSetCollection);
        GraphNode node = headNode;
        int count = 0;
        while(node != null && count++ < GRAPH_MAX_PROCESS_STEPS ) {
            node = node.process(graphPacket);
            if( node == null ) {
                System.out.println("Graph: null node, finished");
            }
        }
        graphPacket.outputSetCollection.display();
        // add sets to SetData
        SetData.getInstance().addSets(graphPacket.outputSetCollection.getSets());
        ConsoleWindowOut.getInstance().println("number of sets in SetData: "+SetData.getInstance().getSets().size());
        graphPacket = null;
        ConsoleWindowOut.getInstance().freeIO();
    }

    // ------------------------------------------------------
    public boolean generateRandom() {
        // get settings
        int minNodes = GraphSettings.getMinNumGraphNodes();
        int maxNodes = GraphSettings.getMaxNumGraphNodes();
        
        InputOutput io = IOProvider.getDefault().getIO ("Graph builder", true);
        // choose number of nodes
        int numNodes = minNodes + (int)((float)(maxNodes-minNodes)*Math.random());
        ConsoleWindowOut.getInstance().println( "making " + numNodes + " random nodes (plus start node)");
        // create nodes
        for( int i = 0 ; i < numNodes ; i++ ) {
            int selectedNode = Utils.getIdxFromProbTable(STANDARD_NODE_SELECTION_PROBS);
            System.out.println("\ncreating node "+i+" of "+numNodes);
            GraphNode node = null;
            int type = (selectedNode+1);
            System.out.println("Looking for type "+type);
            List<GraphNodeInfo> infos = GraphResourceService.getInstance().getAllGraphNodesInfo(graphNodeResourceName,true);
            List<GraphNodeInfo> rightTypeInfos = new ArrayList<GraphNodeInfo>();
            for( GraphNodeInfo info : infos ) {
                if( info != null ) {
                    if( info.getType() == type ) {
                        rightTypeInfos.add(info);
                    }
                }
            }
            System.out.println(rightTypeInfos.size()+" of right type");
            if( !rightTypeInfos.isEmpty() ) {
                int idx = Utils.getIdxFromProbTable(Utils.createUniformProbTable(rightTypeInfos.size()));
                System.out.println("choosing node "+rightTypeInfos.get(idx).getGraphNodeName());
                node = GraphResourceService.getInstance().createGraphNodeByName(
                        rightTypeInfos.get(idx).getGraphNodeName());
            }
            if( node != null ) {
                ConsoleWindowOut.getInstance().println( "created node: "+node.getName());
                System.out.println("created node: "+node.getName());
                node.randomiseParameters();
                addNode(node);
            } else {
                ConsoleWindowOut.getInstance().println("NULL node");
                System.out.println("NULL node");
                i--;
            }
        }
        // connect nodes (we will connect ALL possible connections)
        for( GraphNode fromNode : allNodes ) {
            if( !fromNode.nextNodesSet() ) {
                ConsoleWindowOut.getInstance().println( "**Connecting node: "+fromNode.getName());
                // create prob table with connection to self weighted down
                int fromNodeType = fromNode.getType();
                float connectionWeights[] = new float[allNodes.size()];
                ConsoleWindowOut.getInstance().print("connection weights: ");
                for( int i = 0 ; i < connectionWeights.length ; i++ ) {
                    //connectionWeights[i] = (fromNode == allNodes.get(i) ? (fromNode.willAllowSelfConnection() ? 1 : 0) : 4);
                    connectionWeights[i] = (float)transitionsForBuilding.get(fromNodeType,allNodes.get(i).getType());
                    ConsoleWindowOut.getInstance().print(connectionWeights[i]+", ");
                }
                ConsoleWindowOut.getInstance().println(" ]]");
                float connectionProbTable[] = Utils.createWeightedProbTable(connectionWeights);

                ConsoleWindowOut.getInstance().print( "probs: ");
                for( int i = 0 ; i < connectionProbTable.length ; i++ ) {
                    ConsoleWindowOut.getInstance().print( connectionProbTable[i] + "," );
                }
                ConsoleWindowOut.getInstance().println( " ]]");

                ConsoleWindowOut.getInstance().println( "Num next nodes: "+fromNode.maxNextConnections);
                for( int i = 0 ; i < fromNode.maxNextConnections ; i++ ) {
                    ConsoleWindowOut.getInstance().println( "\n   setting from connection " + i + " ] ");
                    boolean done = false;
                    int tries = 0;
                    while(!done && tries++ < NODE_CONNECTION_MAX_TRIES ) {
                        int idx = Utils.getIdxFromProbTable( connectionProbTable );
                        ConsoleWindowOut.getInstance().print( idx + " ");
                        if( allNodes.get(idx).willAcceptConnectionFrom(fromNode) ) {
                            if( fromNode.addNext(i,allNodes.get(idx)) ) {
                                ConsoleWindowOut.getInstance().println( "\n   SET! to node "+allNodes.get(idx).getName());
                                done = true;
                            } else {
                                ConsoleWindowOut.getInstance().println( "\n   couldn't set to node "+allNodes.get(idx).getName());
                            }
                        }
                    }
                }
                ConsoleWindowOut.getInstance().println("");
            }
        }
        // delete any dead nodes (nodes with nothing going to them)
        boolean changeMade = true;
        while( changeMade ) {
            changeMade = false;
            for( int i = 0 ; i < allNodes.size() ; i++ ) {
                if( !(allNodes.get(i) instanceof StartNode) ) {
                    ConsoleWindowOut.getInstance().println( allNodes.get(i).getName()+" parents:");
                    int numPrevious = 0;
                    for( GraphNode node : allNodes.get(i).previous ) {
                        if( node != null ) {
                            ConsoleWindowOut.getInstance().println( "\t\t"+node.getName());
                            numPrevious++;
                        }
                    }
                    ConsoleWindowOut.getInstance().println( "\t\thas "+numPrevious+" parents");
                    if( numPrevious == 0 || (numPrevious == 1 && allNodes.get(i) == allNodes.get(i).previous.get(0)) ) {
                        ConsoleWindowOut.getInstance().println( "removing "+allNodes.get(i).getName());
                        allNodes.get(i).remove();
                        allNodes.remove(i);
                        changeMade = true;
                        break;
                    }
                }
            }
        }

        // remove nodes which execution flow cannot possibly reach
        ConsoleWindowOut.getInstance().println( "\nverifying nodes are reachable from StartNode...");
        ArrayList<GraphNode>verifiedNodes = new ArrayList<GraphNode>();
        headNode.verify(verifiedNodes);
        ConsoleWindowOut.getInstance().println("Removing unverified nodes...");
        boolean noChange = false;
        while(!noChange) {
            noChange = true;
            for(GraphNode node : allNodes) {
                if( !verifiedNodes.contains(node) ) {
                    ConsoleWindowOut.getInstance().println( "\tremoved "+node.getName()+", not reachable"); 
                    node.remove();
                    allNodes.remove(node);
                    noChange = false;
                    break;
                }
            }
        }
        ConsoleWindowOut.getInstance().println("done");

        // finally, test to see if there is at least one Pick and one Put
        boolean atLeastOnePick = false;
        boolean atLeastOnePut = false;
        for( GraphNode node : allNodes ) {
            if( node.getType() == GraphNode.SAMPLE ) {
                    atLeastOnePick = true;
            } else if( node.getType() == GraphNode.WRITE ) {
                    atLeastOnePut = true;
            }
        }
        setChanged();   //observable send notification
        finishChanges();
        return atLeastOnePick & atLeastOnePut;
    }

    public class GraphPacket {
        public SetCollection inputSetCollection;
        public SetCollection outputSetCollection;
        public Set tempSet;

        public GraphPacket( SetCollection _inputSetCollection, SetCollection _outputSetCollection ) {
            inputSetCollection = _inputSetCollection;
            outputSetCollection = _outputSetCollection;
            tempSet = null;
            //phraseSet = new Set("Phrase",phraseLength,new PVector(-1,-1));
        }

        public void displayVariables() {
            if( tempSet != null ) {
                tempSet.display();
            } else {
                ConsoleWindowOut.getInstance().println("GraphPacket:displayVariables tempSet is null");
            }
        }
    }
}