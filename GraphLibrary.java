import java.io.*; 
import java.util.*; 

public class GraphLibrary {
	
	
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
		SLLQueue<V> BFSQueue = new SLLQueue<V>();
		Graph<V, E> result = new AdjacencyMapGraph<V, E>(); 
		BFSQueue.enqueue(source);
		result.insertVertex(source);
		// keeps track of visited verticies, just because a vertex is in graph we still need to visit its neighbors
		Set<V> visited = new HashSet<V>();
		V currVertex = source; 
		 
		while (!BFSQueue.isEmpty()) {
			// make sure we can dequeue
			try {
				currVertex = BFSQueue.dequeue();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if (!visited.contains(currVertex)) {
				visited.add(currVertex); 
				// loop over neighbors 
				for (V neighbor : g.outNeighbors(currVertex)) {
					if (!result.hasVertex(neighbor)) { 
						// enqueue neighbor, insert neighbor into graph and insert directed edge from neighbor to current
						BFSQueue.enqueue(neighbor); 
						result.insertVertex(neighbor);
						result.insertDirected(neighbor, currVertex, g.getLabel(neighbor, currVertex));
					}
				} 
			} 
		}
		return result; 
	}

	public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {	
		List<V> result = new ArrayList<V>(); 
		V currVertex = v;  
		// traverse graph until you reach root
		while (tree.outDegree(currVertex) > 0) {
			// each has only one out neighbor
			for (V neighbor : tree.outNeighbors(currVertex)) {
				result.add(currVertex); 
				currVertex = neighbor; 
			}
		} 
		result.add(currVertex); 
		return result; 
	}
	
	
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
		Set<V> result = new HashSet<V>(); 
		
		for (V currVertex : graph.vertices()) {
			// add to set if subgraph doesn't contain vertex 
			if (!subgraph.hasVertex(currVertex)) {
				result.add(currVertex); 
			}
		}
		return result; 
	}
	
	
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {	
		
		double totalLength = avgSeparationHelper(tree, root, 0); 
		double totalVerticies = (double) tree.numVertices(); 
		return totalLength / totalVerticies; 
	}
	
	public static <V, E> double avgSeparationHelper(Graph<V, E> tree, V currVertex, double TotalSoFar) {
		// recursively calculate the length, add to TotalSoFar on each level
		double total = TotalSoFar; 
		// base case, when you reach leaf just return total
		if (tree.inDegree(currVertex) == 0) {
			return total; 
		}
		// call for each neighbor
		for (V neighbor : tree.inNeighbors(currVertex)) {
			total += avgSeparationHelper(tree, neighbor, TotalSoFar+1); 
		}
		return total; 	
	}
}
