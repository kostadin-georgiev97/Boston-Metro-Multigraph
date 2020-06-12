package Model;

import java.util.*;

public class MultiGraph implements MultiGraphADT {

    private Map<Node, List<Edge>> graph;

    public MultiGraph() {
        graph = new HashMap<>();
    }

    // Returns number of nodes in the graph
    @Override
    public int nNodes() {
        return graph.keySet().size();
    }

    // Returns number of edges in the graph
    @Override
    public int nEdges() {
        int numEdges = 0;
        Collection<List<Edge>> edges = graph.values();
        for (List<Edge> currentList : edges) {
            numEdges += currentList.size();
        }
        return numEdges;
    }

    // Adds a Node to the graph
    // Returns an error code appropriately
    @Override
    public boolean addNode(Node node) {
        if(graph.containsKey(node)){
            return false;
        }else {
            graph.put(node, new LinkedList<Edge>());
            return true;
        }
    }

    // Gets a node from the graph if it exists, null otherwise
    @Override
    public Node getNode(int nodeID){
        for(Node n : graph.keySet()){
            if(n.getNodeID() == nodeID){
                return n;
            }
        }
        return null;
    }

    // Adds an Edge to the graph
    // Returns an error code appropriately
    @Override
    public boolean addEdge(Edge edge) {
        Node node = edge.getStartNode();
        if(graph.containsKey(node)) {
            List<Edge> edges = graph.get(node);
            if (!edges.contains(edge)) {
                edges.add(edge);
                graph.replace(node, edges);
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    // Returns if an Edge exists in the graph or not
    // Returns an error code appropriately
    @Override
    public boolean hasEdge(Node node1, Node node2) {
        if(graph.containsKey(node1) && graph.containsKey(node2)){
            List<Edge> edges = graph.get(node1);
            for (Edge e : edges) {
                if(e.getEndNode().getNodeID() == node2.getNodeID()){
                    return true;
                }
            }
            return false;
        }else {
            return false;
        }

    }

    // Returns a List off Nodes which is the shortest path between two nodes
    // It uses the Breadth-First-Search algorithm
    @Override
    public List<Node> shortestPath(Node start, Node end) {

        // Make the path to the destination into a Linked List (since that is what we wish to return)
        List<Node> path = new LinkedList<>();

        Node currentNode = start;

        // Map to keep the route we are following it's kind of like a linked list between the nodes
        Map<Node, Node> previousNodes = new HashMap<>();

        // Set of visited nodes
        Set<Node> explored = new HashSet<>();
        explored.add(currentNode);

        // Queue to add the nodes that have still to be visited
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(currentNode);


        // Using the start node as the first node
        // This loop checks the neighbouring nodes for nodes that haven't been visited yet
        // It also checks if one of the nodes is the destination node
        while (!queue.isEmpty()){
            currentNode = queue.remove();

            if(currentNode.equals(end)){
                previousNodes.put(null, currentNode);
                break;
            }else {
                // Looping through neighbouring nodes
                for (Edge e : graph.get(currentNode)) {
                    Node endNode = e.getEndNode();
                    if (!explored.contains(endNode)) {
                        explored.add(endNode);
                        queue.add(endNode);
                        previousNodes.put(endNode, currentNode);
                    }
                }
            }
        }

        if(!currentNode.equals(end)){
            return path; //Path not found
        }

        for(Node node = end; node != null; node = previousNodes.get(node)){
            path.add(node);
        }

        Collections.reverse(path); // Reverse the list so the path is in the right order

        return path;

    }

}
