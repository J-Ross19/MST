package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;
import structures.Vertex.Neighbor;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		//Create an empty list L of partial trees.
		PartialTreeList listOfPartialTrees = new PartialTreeList();
		
		// Separately for each vertex v in the graph:
		for (Vertex currentVertex : graph.vertices) {
			// Create a partial tree T containing only v.
			PartialTree tempTree = new PartialTree(currentVertex);
			
			// Mark v as belonging to T (this will be implemented in a particular way in the code).
			currentVertex.parent = currentVertex;
			
			// Create a priority queue (heap) P and associate it with T.
			//tempTree.getArcs();
			
			// Insert all of the arcs (edges) connected to v into P. 
			for (Neighbor currentNeighbor = currentVertex.neighbors; 
					currentNeighbor != null; currentNeighbor = currentNeighbor.next) {
				
				// Get reference to MinHeap of PartialTree
				tempTree.getArcs().insert( // Create and insert new arc
						new Arc(currentVertex, currentNeighbor.vertex, currentNeighbor.weight));
				
				// Note: The lower the weight on an arc, the higher its priority.
				
			}
						
			// Add the partial tree T to the list L.
			listOfPartialTrees.append(tempTree);
		}

		return listOfPartialTrees;
	}
	
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		
		
		ArrayList<Arc> minimumSpanningTree = new ArrayList<Arc>();

		while (ptlist.size > 1) {

			PartialTree currentTree = ptlist.remove();
			
			// Set nextArc to highest priority arc where the destination vertex is not already in the MST
			Arc nextArc = currentTree.getArcs().deleteMin();
			
		
			while (nextArc.getv1().getRoot() == nextArc.getv2().getRoot()) { // Check if arcs are in same tree already
				nextArc = currentTree.getArcs().isEmpty() ? null : currentTree.getArcs().deleteMin();
			}

			PartialTree treeRemoved = null;
			
			if (nextArc != null) {
				minimumSpanningTree.add(nextArc);
				
				treeRemoved = ptlist.removeTreeContaining(nextArc.getv2());

			}
			
			currentTree.merge(treeRemoved);
				
			
			
			ptlist.append(currentTree);
			
			

		}
		
		return minimumSpanningTree;
	}
	
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    		throws NoSuchElementException {
    	if (vertex == null || size <= 0) {
    		throw new NoSuchElementException("Unable to perform operation");
    	}
    	
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	
    	Node prev = rear;
    	Node current = rear.next;
    	
		
    	while (rear != current && current.tree.getRoot() != vertex.getRoot()) {
    		prev = current;
    		current = current.next;
    	} 
    	
    	// Last element
    	if (current == rear) {
    		rear = prev;
    	}
       	prev.next = current.next;
    	
    	size--;
    	return current.tree;
    }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


