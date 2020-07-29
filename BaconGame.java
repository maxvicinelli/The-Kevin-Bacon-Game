import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class BaconGame {
	
	static Graph<String, Set<String>> mainGraph = new AdjacencyMapGraph<String, Set<String>>(); 
	static Map<String, String> actorIDs = new HashMap<String, String>(); 
	Map<String, String> movieIDs = new HashMap<String, String>(); 
	Map<String, List<String>> IDtoID = new HashMap<String, List<String>>(); 
	BufferedReader movies;  
	BufferedReader actors; 
	BufferedReader relationships;
	
	static Graph<String, Set<String>> universeCenterGraph; 
	
	public static String universeCenter = "Kevin Bacon"; 
	
	public static final String folder = "PS 4"; 
	public static final String actorFileName = folder + "/actors.txt"; 
	public static final String movieFileName = folder + "/movies.txt"; 
	public static final String relationshipFileName = folder + "/movie-actors.txt"; 
	
	
	
	public BaconGame() throws Exception {
		this.actors = new BufferedReader(new FileReader(actorFileName)); 
		this.movies = new BufferedReader(new FileReader(movieFileName)); 
		this.relationships = new BufferedReader(new FileReader(relationshipFileName));
		
		// create maps and graphs 
		
		makeMaps(); 
		makeGraph(); 
	}
	
	public void makeMaps() throws IOException {
		String position1 = "";   
		// add all actors to actorID map
		while ((position1 = actors.readLine()) != null) {
			String [] splittedActors = position1.split("\\|");
			actorIDs.put(splittedActors[0], splittedActors[1]); 
		}
		
		String position2 = "";  
		// add all movies to movieID map, ID is key, name is value
		while ((position2 = movies.readLine()) != null) { 
			String[] splittedMovies = position2.split("\\|"); 
			movieIDs.put(splittedMovies[0], splittedMovies[1]); 
		}
		
		String position3 = "";  
		// create relationships map with movie ID as key and a list of actors as value
		while ((position3 = relationships.readLine()) != null) { 
			String[] splittedRelationships = position3.split("\\|"); 
			if (IDtoID.containsKey(splittedRelationships[0])) {
				IDtoID.get(splittedRelationships[0]).add(splittedRelationships[1]); 
			}
			else {
				List<String> newList = new ArrayList<String>(); 
				newList.add(splittedRelationships[1]); 
				IDtoID.put(splittedRelationships[0], newList); 
			} 
		}
	}
	
	public void makeGraph() {
		// insert vertexes 
		for (String actorID : actorIDs.keySet()) {
			String actorName = actorIDs.get(actorID); 
			mainGraph.insertVertex(actorName);
		}
		// add in an edge between actors that share a movie
		for (String movieID : IDtoID.keySet()) {
			String movieName = movieIDs.get(movieID); 
			List<String> actorList = IDtoID.get(movieID); 
			for (String actorID1 : actorList) {
				String actor1Name= actorIDs.get(actorID1); 
				
				// for each actor, loop over all other actors to find combos 
				for (String actorID2 : actorList) {
					if (!actorID1.equals(actorID2)) {
						String actor2Name = actorIDs.get(actorID2); 
						// either create a new movie list as the label or add to the list if already created
						if (mainGraph.hasEdge(actor1Name, actor2Name)) {
							mainGraph.getLabel(actor1Name, actor2Name).add(movieName); 
						}
						else {
							Set<String> movieLabelSet = new HashSet<String>(); 
							movieLabelSet.add(movieName); 
							mainGraph.insertUndirected(actor1Name, actor2Name, movieLabelSet);
						}
					}
				}
			}
		} 
	}
	public static int bCoUcompareHelper(String s1, String s2) {
		// comparison function for best Center of Universe method 
		double s2Rating = GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, s2), s2); 
		double s1Rating = GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, s1), s1); 
		if (s1Rating > s2Rating) {
			return 1; 
		}
		else if (s1Rating < s2Rating) {
			return -1; 
		}
		else {
			return 0; 
		}
	}
	
	public static int wCoUcompareHelper(String s1, String s2) {
		// comparison function for worst center of universe method
		double s2Rating = GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, s2), s2); 
		double s1Rating = GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, s1), s1); 
		
		if (s1Rating > s2Rating) {
			return -1; 
		}
		else if (s1Rating < s2Rating) {
			return 1; 
		}
		else {
			return 0; 
		}
	}
	
	public static void worstCentersofUniverse(int number) {
		
		

		PriorityQueue<String> PQ = new PriorityQueue<String>( (String s1, String s2) -> wCoUcompareHelper(s1, s2));
		
		// add all verticies to PQ
		for (String actorID : actorIDs.keySet()) {
			String actorName = actorIDs.get(actorID); 
			if (universeCenterGraph.hasVertex(actorName)) {
				PQ.add(actorName); 
			}
		}
		// remove from PQ until reach correct amount
		List<String> result = new ArrayList<String>();
		while  (result.size() < number && !PQ.isEmpty()) {
			String removedActorName = PQ.remove(); 
			result.add(removedActorName);  
		}
		System.out.println("The worst " + number + " centers of the universe are:" );
		for (String actorName : result) {
			System.out.println(actorName + " with average degree of separation of " + GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, actorName), actorName));
		}
	} 
	
	public static void listByDegree(int low, int high) {
		// print verticies with outdegree between low and high 
		PriorityQueue<String> PQ = new PriorityQueue<String>( (String s1, String s2) -> mainGraph.outDegree(s1) - mainGraph.outDegree(s2)); 
		// add all verticies to PQ
		for (String actorID : actorIDs.keySet()) {
			String actorName = actorIDs.get(actorID); 
			PQ.add(actorName); 
		}
		String currActor = PQ.remove(); 
		List<String> result = new ArrayList<String>(); 
		// until we reach an outdegree that's too high or the PQ becomes empty
		while (mainGraph.outDegree(currActor) <= high && !PQ.isEmpty()) {
			if (mainGraph.outDegree(currActor) >= low) {
				result.add(currActor); 
			}
			currActor = PQ.remove(); 
		}
		// add last item in PQ bc i remove at the end
		if (mainGraph.outDegree(currActor) < high && mainGraph.outDegree(currActor) > low) {
			result.add(currActor); 
		}
		System.out.println("The actors with out degrees between " + low + " and " + high + " are:");
		for (String actorName : result ) {
			System.out.println(actorName + " with out degree of " + mainGraph.outDegree(actorName));
		}
	}
	
	public static void infiniteSeparation() {
		// print verticies not connected to universe center 
		Set<String> notConnected = GraphLibrary.missingVertices(mainGraph, universeCenterGraph); 
		System.out.println("Actors not connected to " + universeCenter + " are " + notConnected);
		
	}
	public static void findThePath(String goal) {
		// prints the number and path 
		List<String> result = GraphLibrary.getPath(universeCenterGraph, goal); 
		int size = result.size() - 1; // doesn't include origin
		System.out.println(goal+"'s number is " + size);
		
		// print the path
		for (int i = 0; i < result.size()-1; i++) {
			String actor1 = result.get(i); 
			String actor2 = result.get(i+1); 
			Set<String> movies = mainGraph.getLabel(actor1, actor2); 
			System.out.println(actor1+ " appeared in " + movies + " with " + actor2);
		}
	}
	
	public static int sHelper(String s1, String s2) {
		// comparison for sorted by separation (s)
		int a = GraphLibrary.getPath(universeCenterGraph, s1).size() -1; 
		int b = GraphLibrary.getPath(universeCenterGraph,  s2).size()-1; 
		
		if (a > b) {
			return 1; 
		}
		else if (a < b) {
			return -1; 
		}
		else {
			return 0; 
		}
		
	}
	public static void sortedBySeparation(int low, int high) {
		
		PriorityQueue<String> PQ = new PriorityQueue<String>((String s1, String s2) -> sHelper(s1, s2)); 
		List<String> result = new ArrayList<String>(); 
		
		// loop over all verticies 
		for (String vertex : universeCenterGraph.vertices()) {
			// get length of path
			int length = GraphLibrary.getPath(universeCenterGraph,  vertex).size()-1; 
			if (length >= low && length <= high) { // if correct length then add it PQ
				PQ.add(vertex); 
			}
		}
		
		while (!PQ.isEmpty()) { // add to resulting list until PQ is empty
			result.add(PQ.remove()); 
		}
		System.out.println("The actors with degrees of separation between " + low + " and " + high + " are:");
		for (String actorName : result) {
			int separation = GraphLibrary.getPath(universeCenterGraph,  actorName).size() -1; 
			System.out.println(actorName + " with degree of separation of " + separation);
		}
	}
	
	public static void bestCentersofUniverse(int number) {
		// FYI this takes like 4 mins to complete 
		// same as min, but uses different comparison function
		PriorityQueue<String> PQ = new PriorityQueue<String>( (String s1, String s2) -> bCoUcompareHelper(s1, s2)); 
		for (String actorID : actorIDs.keySet()) {
			String actorName = actorIDs.get(actorID); 
			if (universeCenterGraph.hasVertex(actorName)) {
				PQ.add(actorName);
			}
		}
		List<String> result = new ArrayList<String>();
		
		while  (result.size() < number && !PQ.isEmpty()) {
			String removedActorName = PQ.remove(); 
			result.add(removedActorName); 
		}
		System.out.println("The best " + number + " centers of the universe are:");
		for (String actorName : result) {
			System.out.println(actorName + " with average degree of separation of " + GraphLibrary.averageSeparation(GraphLibrary.bfs(mainGraph, actorName), actorName));
		}
	}
	
	public static void main(String[] args) throws Exception { 
		new BaconGame();
		Scanner in = new Scanner(System.in); 
		System.out.println("Commands:\n" + 
				"c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" + 
				"d <low> <high>: list actors sorted by degree, with degree between low and high\n" + 
				"i: list actors with infinite separation from the current center\n" + 
				"p <name>: find path from <name> to current center of the universe\n" + 
				"s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" + 
				"u <name>: make <name> the center of the universe\n" + 
				"q: quit game");
		
		
		String text = ""; 
		
		universeCenterGraph = GraphLibrary.bfs(mainGraph, universeCenter); 
		 
		// loop until we input q 
		while (!text.equals("q")) {
			System.out.println(universeCenter + " game >");
			// wait until user input
			text = in.nextLine();
				if (text.isBlank()) { // boundary case: nothing entered
					System.out.println("please enter something");
				}
				else if (text.toCharArray()[0] == 'c') {
					try { 
						// if a negative number, get worst centers 
						if (text.toCharArray()[2] == '-') {
							String[] a = text.split("\\ "); 
							String numberString = a[1];  
							int number = Integer.parseInt(numberString); 
							worstCentersofUniverse(Math.abs(number)); 
						}
						else {
							// if positive number, get best centers 
							String[] a = text.split("\\ "); 
							String numberString = a[1];  
							int number = Integer.parseInt(numberString); 
							bestCentersofUniverse(number);
						}
					}
					catch (Exception e) {
						// if you enter c and then not a number
						System.out.println("that command is of invalid format");
					}
				
				}
				
				else if (text.toCharArray()[0] == 'd') {
					try {
						// find people with degree of separation between high and low
						String[] a = text.split("\\ "); 
						
						String lowString = a[1]; 
						String highString = a[2]; 
						
						int low = Integer.parseInt(lowString); 
						int high = Integer.parseInt(highString); 
						
						if (low > high) {
							System.out.println("please enter low less than high");
						}
						else {
							listByDegree(low, high); 
						}
					}
					catch(Exception e) {
						// if you don't enter a number after d 
						System.out.println("that command is of invalid format");
					}
				}
				
				else if (text.toCharArray()[0] == 'i') {
					infiniteSeparation(); 
				}
				
				else if (text.toCharArray()[0] == 'p') {
					// get the name by concatenating one char at a time to the end of the string
					
					String name = ""; 
					for (int i=2; i < text.toCharArray().length; i++) {
						name += text.toCharArray()[i]; 
					}
					if (!actorIDs.containsValue(name)) {
						System.out.println("invalid name");
					}
					else {
						findThePath(name); 
					}
				}
				
				else if (text.toCharArray()[0] == 's') {
					try {
					// prints verticies with path length from center between low and high
						String[] a = text.split("\\ "); 
						
						String lowString = a[1]; 
						String highString = a[2]; 
					
						
						int low = Integer.parseInt(lowString);
						
						int high = Integer.parseInt(highString); 
						
						
						if (low > high) {
							System.out.println("please enter low less than high");
						}
						else {  
							sortedBySeparation(low, high); 
						}
					}
					catch(Exception e) {
						System.out.println("invalid format");
					}
				}
				
				else if (text.toCharArray()[0]==  'u') {
					// update center to input and recall BFS
					String name = ""; 
					for (int i = 2; i < text.toCharArray().length; i++) {
						name += text.toCharArray()[i]; 
					}
					if (!actorIDs.containsValue(name)) {
						System.out.println("invalid name");
					}
					else {
						universeCenter = name;  
						universeCenterGraph = GraphLibrary.bfs(mainGraph, universeCenter); 
						Set<String> set = GraphLibrary.missingVertices(mainGraph, universeCenterGraph); 
						int connected = mainGraph.numVertices() - set.size(); 
						double avgSep = GraphLibrary.averageSeparation(universeCenterGraph, universeCenter); 	 
						System.out.println(universeCenter + " is now the center of the acting universe, connected to " + connected + "/" + mainGraph.numVertices() + " actors with average separation " + avgSep );
					}
				}
				
				else {
					// quit 
					if (text.toCharArray()[0] != 'q') {
						System.out.println("character invalid, please re-enter");
					}
				}
		
	}
	System.out.println("Game quit");
	in.close(); 
}}

