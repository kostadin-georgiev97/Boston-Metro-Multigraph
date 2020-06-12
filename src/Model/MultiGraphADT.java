package Model;

import java.util.List;

/**
 * A simple interface representing a multi-graph.
 * Apart from the trivial methods for adding nodes,
 * edges, etc., there is a method for finding
 * the quickest path between two nodes.
 */

/**
 * @author Kostadin Georgiev
 */
public interface MultiGraphADT {

	/**
	 * Returns the number of nodes.
	 */
	public int nNodes();
	
	/**
	 * Returns the number of edges.
	 */
	public int nEdges();
	
	/**
	 * Adds a new node to the graph.
	 */
	public boolean addNode(Node node);

	public Node getNode(int nodeID);
	
	/**
	 * Adds a new edge.
	 * @param edge
	 */
	public boolean addEdge(Edge edge);
	
	/**
	 * Returns true if there's an edge between node1 and node2 and false otherwise.
	 * @param node1
	 * @param node2
	 */
	public boolean hasEdge(Node node1, Node node2);
	
	/**
	 * Finds the shortest path between two nodes of the graph.
	 * @param node1
	 * @param node2
	 */
	public List<Node> shortestPath(Node node1, Node node2);

}
