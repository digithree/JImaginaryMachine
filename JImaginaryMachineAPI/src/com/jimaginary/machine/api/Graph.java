/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.api;

import com.jimaginary.machine.math.Bernoulli;
import com.jimaginary.machine.math.Matrix;
import java.util.ArrayList;
import java.util.Collections;
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

    // process
    GraphPacket graphPacket, lastGraphPacket;
    

    public Graph( SetCollection _inputSetCollection, SetCollection _outputSetCollection ) {
        inputSetCollection = _inputSetCollection;
        outputSetCollection = _outputSetCollection;
        allNodes = new ArrayList<GraphNode>();
        shakeOut = false;
        transitionsForBuilding = new Matrix(NUM_NODES,NUM_NODES);
        transitionsForBuilding.set( MARKOV_CHAIN_NODE_SELECTION_PROBS );
        graphPacket = new GraphPacket(inputSetCollection,outputSetCollection);
        lastGraphPacket = null; // why use this?
        setupManditoryNodes();
        setChanged();
    }
    
    public SetCollection getInputSetCollection() {
        return inputSetCollection;
    }
    
    public SetCollection getOutputSetCollection() {
        return outputSetCollection;
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

    private void setupManditoryNodes() {
        System.out.println("Graph : setupManditoryNodes");
        // add StartNode to head
        headNode = (GraphNode)new StartNode();
        allNodes.add(headNode);
        // add manditory nodes from input set collection
        GraphNode inNodes[] = inputSetCollection.generateManditoryFirstModifyNodes();
        if( inNodes != null ) {
            for( GraphNode node : inNodes ) {
                node.randomiseParameters();
                allNodes.add(node);
            }
        }
        // add manditory nodes from input set collection
        GraphNode outNodes[] = outputSetCollection.generateManditoryFirstModifyNodes();
        if( outNodes != null ) {
            for( GraphNode node : outNodes ) {
                node.randomiseParameters();
                allNodes.add(node);
            }
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
    
    public int addNodeByName( String nodeName ) {
        GraphNode node = inputSetCollection.getGraphNodeByName(nodeName);
        if( node == null ) {
            node = outputSetCollection.getGraphNodeByName(nodeName);
        }
        if( node == null ) {
            return -1;
        }
        // else, we got a node
        allNodes.add(node);
        setChanged();
        return node.getId();
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
                if( allNodes.get(i).nextContains(allNodes.get(j)) ) {
                    adjacencyMatrix.set(i, j, 1);
                }
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
    }

    // ------------------------------------------------------
    public boolean generateRandom( int minNodes, int maxNodes ) {
        InputOutput io = IOProvider.getDefault().getIO ("Graph builder", true);
        // choose number of nodes
        int numNodes = minNodes + (int)((float)(maxNodes-minNodes)*Math.random());
        io.getOut().println( "making " + numNodes + " random nodes (plus start node)");
        // create nodes
        for( int i = 0 ; i < numNodes ; i++ ) {
            int selectedNode = Utils.getIdxFromProbTable(STANDARD_NODE_SELECTION_PROBS);
            GraphNode node = null;
            switch( selectedNode+1 ) {
                case GraphNode.SAMPLE:
                    node = inputSetCollection.generateSampleNode();
                    break;
                case GraphNode.WRITE:
                    node = outputSetCollection.generateWriteNode();
                    break;
                case GraphNode.CHOICE:
                    node = (GraphNode)new Choice2Node();
                    break;
                case GraphNode.MODIFY:
                    int modifyInOrOut = Utils.getIdxFromProbTable(STANDARD_NODE_SELECTION_PROBS_MODIFY);
                    if( modifyInOrOut == 0 ) {
                            node = inputSetCollection.generateModifyNode();
                    } else {
                            node = outputSetCollection.generateModifyNode();
                    }
            }
            if( node != null ) {
                io.getOut().println( "created node: "+node.name);
                node.randomiseParameters();
                addNode(node);
            } else {
                io.getOut().println("NULL node");
                i--;
            }
        }
        // connect nodes (we will connect ALL possible connections)
        for( GraphNode fromNode : allNodes ) {
            if( !fromNode.nextNodesSet() ) {
                io.getOut().println( "**Connecting node: "+fromNode.name);
                // create prob table with connection to self weighted down
                int fromNodeType = fromNode.getType();
                float connectionWeights[] = new float[allNodes.size()];
                io.getOut().print("connection weights: ");
                for( int i = 0 ; i < connectionWeights.length ; i++ ) {
                    //connectionWeights[i] = (fromNode == allNodes.get(i) ? (fromNode.willAllowSelfConnection() ? 1 : 0) : 4);
                    connectionWeights[i] = (float)transitionsForBuilding.get(fromNodeType,allNodes.get(i).getType());
                    io.getOut().print(connectionWeights[i]+", ");
                }
                io.getOut().println(" ]]");
                float connectionProbTable[] = Utils.createWeightedProbTable(connectionWeights);

                io.getOut().print( "probs: ");
                for( int i = 0 ; i < connectionProbTable.length ; i++ ) {
                    io.getOut().print( connectionProbTable[i] + "," );
                }
                io.getOut().println( " ]]");

                io.getOut().println( "Num next nodes: "+fromNode.maxNextConnections);
                for( int i = 0 ; i < fromNode.maxNextConnections ; i++ ) {
                    io.getOut().println( "\n   setting from connection " + i + " ] ");
                    boolean done = false;
                    int tries = 0;
                    while(!done && tries++ < NODE_CONNECTION_MAX_TRIES ) {
                        int idx = Utils.getIdxFromProbTable( connectionProbTable );
                        io.getOut().print( idx + " ");
                        if( allNodes.get(idx).willAcceptConnectionFrom(fromNode) ) {
                            if( fromNode.addNext(i,allNodes.get(idx)) ) {
                                io.getOut().println( "\n   SET! to node "+allNodes.get(idx).name);
                                done = true;
                            } else {
                                io.getOut().println( "\n   couldn't set to node "+allNodes.get(idx).name);
                            }
                        }
                    }
                }
                io.getOut().println("");
            }
        }
        // delete any dead nodes (nodes with nothing going to them)
        boolean changeMade = true;
        while( changeMade ) {
            changeMade = false;
            for( int i = 0 ; i < allNodes.size() ; i++ ) {
                if( !(allNodes.get(i) instanceof StartNode) ) {
                    io.getOut().println( allNodes.get(i).name+" parents:");
                    int numPrevious = 0;
                    for( GraphNode node : allNodes.get(i).previous ) {
                        if( node != null ) {
                            io.getOut().println( "\t\t"+node.name);
                            numPrevious++;
                        }
                    }
                    io.getOut().println( "\t\thas "+numPrevious+" parents");
                    if( numPrevious == 0 || (numPrevious == 1 && allNodes.get(i) == allNodes.get(i).previous.get(0)) ) {
                        io.getOut().println( "removing "+allNodes.get(i).name);
                        allNodes.get(i).remove();
                        allNodes.remove(i);
                        changeMade = true;
                        break;
                    }
                }
            }
        }

        // remove nodes which execution flow cannot possibly reach
        io.getOut().println( "\nverifying nodes are reachable from StartNode...");
        ArrayList<GraphNode>verifiedNodes = new ArrayList<GraphNode>();
        headNode.verify(verifiedNodes);
        io.getOut().println("Removing unverified nodes...");
        boolean noChange = false;
        while(!noChange) {
            noChange = true;
            for(GraphNode node : allNodes) {
                if( !verifiedNodes.contains(node) ) {
                    io.getOut().println( "\tremoved "+node.name+", not reachable"); 
                    node.remove();
                    allNodes.remove(node);
                    noChange = false;
                    break;
                }
            }
        }
        io.getOut().println("done");

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
                System.out.println("GraphPacket:displayVariables tempSet is null");
            }
        }
    }
    
    public class StartNode extends GraphNode {
        StartNode() {
            super("Start",START,0,1);
            acceptsConnection = false;
            description = "Process starts";
        }

        // don't need processing, but just for console output
        @Override
        public GraphNode process( GraphPacket graphPacket ) {
            System.out.println( "\n\n"+name +" \t\t- starting processing ");
            return next[0];
        }
    }

    // PUT NOTE
    public class Choice2Node extends GraphNode {
        final float NODE_CHOICE_MIN_VAL = 0.15f;
        
        private final int PARAM_CHOICE = 0;
        private final float CHOICE_PROB = 0.5f;

        Choice2Node() {
            super("2 Choice",CHOICE,1,2);
            //default values for choices, uniform
            setParameter(PARAM_CHOICE, new Bernoulli(CHOICE_PROB)); 
            getParameter(PARAM_CHOICE).setAlwaysRandomise(true);
        }

        @Override
        public void refreshDescription() {
            description = "Choose A P["+String.format("%.2f",parameters[0].getParameter(0))+"] or B P["
                +String.format("%.2f",(1.f-parameters[0].getParameter(0)))+"]";
        }

        // override
        @Override
        public GraphNode process( GraphPacket graphPacket ) {
            int choice = (int)getParameter(PARAM_CHOICE).evaluate();
            
            System.out.println(name+" \t\t- choose "+(choice==0?"A":"B"));
            return next[choice];
        }
    }

}