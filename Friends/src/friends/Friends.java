package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		/** COMPLETE THIS METHOD **/
		Boolean[] visited = new Boolean[g.members.length];
		Queue<Integer> q = new Queue<>();
		ArrayList<String> result = new ArrayList<>();
		ArrayList<Edge> edges = new ArrayList<>();
		boolean end = false;
		if(!g.map.containsKey(p1) || !g.map.containsKey(p2)) {
			return null;
		}
		for(int i= 0; i< visited.length; i++) {
			visited[i] = false;
		}
		if(p1.equals(p2)) {
			return null;
		}
		int first = g.map.get(p1);
		q.enqueue(first);
		visited[first] = true;
		while(!q.isEmpty()) {
			if(end == true) {
				break;
			}
			int curr = q.dequeue();
			visited[curr] = true;
			Person currentp = g.members[curr];
			Friend ptr = currentp.first;
			if(currentp.name.equals(p2)) {
				Edge edgeofVerts = new Edge(curr, ptr.fnum);
				edges.add(edgeofVerts);
				break;
			}
			while(ptr != null) {
				if(!visited[ptr.fnum]) {
					String neighbor = g.members[ptr.fnum].name;
					q.enqueue(ptr.fnum);
					visited[ptr.fnum] = true;
					if(neighbor.equals(p2)) {
						Edge edgeofVerts = new Edge(curr, ptr.fnum);
						edges.add(edgeofVerts);
						end = true;
						break;
					}
					Edge edgeofVerts = new Edge(curr, ptr.fnum);
					edges.add(edgeofVerts);
				}
				ptr = ptr.next;
			}
		}
		ArrayList<String> shortest = new ArrayList<>();
		int start = g.map.get(p1);
		int ptr = edges.size()-1;
		int s = g.map.get(p2);
		while(ptr >= 0) {
			if(edges.get(ptr).v2 == s) {
				shortest.add(0, g.members[s].name);
				s = edges.get(ptr).v1;
				if(s == start) {
					shortest.add(0, g.members[s].name);
					break;
				}
			}
			ptr--;
		}
		result = shortest;
		if(result.isEmpty()) {
			return null;
		}
		return result;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		/** COMPLETE THIS METHOD **/
		ArrayList<String> checkfornull = new ArrayList<>();
		if(school == null) {
			return null;
		}
		ArrayList<ArrayList<String>> cliques = new ArrayList<>();
		Boolean[] visited = new Boolean[g.members.length];
		for(int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		for(int i = 0; i < g.members.length; i++) {
			if(g.members[i].school != null && g.members[i].school.equals(school)) {
				if(!visited[i]) {
					visited[i] = true;
					checkfornull = bfs(i, g, school, visited);
					if(checkfornull != null) {
						cliques.add(checkfornull);
					}
				}
			}
		}
		if(cliques.isEmpty()) {
			return null;
		}
		return cliques;
		
	}
	private static ArrayList<String> bfs (int v, Graph g, String school, Boolean[] visited){
		ArrayList<String> M = new ArrayList<>();
		Queue<Integer> bfsq	= new Queue<>();
		int first = v;
		bfsq.enqueue(v);
		visited[v] = true;
		M.add(g.members[v].name);
		while(!bfsq.isEmpty()) {
			int currenti = bfsq.dequeue();
			visited[currenti] = true;
			Person currentp = g.members[currenti];
			Friend f = currentp.first;
			while(f!=null && g.members[f.fnum].school != null && g.members[f.fnum].school.equals(school)) {
				if(!visited[f.fnum]) {
					bfsq.enqueue(f.fnum);
					M.add(g.members[f.fnum].name);
					visited[f.fnum] = true;
				}
				f = f.next;
			}
		}
		return M;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		/** COMPLETE THIS METHOD **/
		Integer[] backnum = new Integer[g.members.length];
		Integer[] dfsnum = new Integer[g.members.length];
		Boolean[] visited = new Boolean[g.members.length];
		int backnumber = 0;
		int dfsnumber = 0;
		ArrayList<String> connectors = new ArrayList<>();
		Boolean[] recursed = new Boolean[g.members.length];
		int previous = 0;
		for(int i =0; i < visited.length; i++) {
			visited[i] = false;
			recursed[i] = false;
			dfsnum[i] = 0;
			backnum[i] = 0;
		}
		for(int i = 0; i < g.members.length; i++) {
			if(!visited[i]) {
				dfsconnectors(i, i, recursed, connectors, i, backnum, dfsnum, visited, g, backnumber, dfsnumber);
			}
		}
		if(connectors.isEmpty()) {
			return null;
		}
		return connectors;
		
	}
	private static void dfsconnectors(int prev, int started, Boolean[] recursed, ArrayList<String> connectors, int i, Integer[] backnum, Integer[] dfsnum, Boolean[] visited, Graph g, int backnumber, int dfsnumber) {
		if(visited[i]) {
			return;
		}
		visited[i] = true;
		dfsnum[i] = dfsnum[prev] + 1;
		backnum[i] = dfsnum[i];
		Friend nbr = g.members[i].first;
		while(nbr != null) {
			if(visited[nbr.fnum]) {
				backnum[i] = Math.min(backnum[i], dfsnum[nbr.fnum]);
			}
			else {
				dfsconnectors(i, started, recursed, connectors, nbr.fnum, backnum, dfsnum,visited, g, backnumber, dfsnumber);
				if(dfsnum[i] <= backnum[nbr.fnum] && !connectors.contains(g.members[i].name)) {
					if(i != started || recursed[i] == true) {
						connectors.add(g.members[i].name);
					}
				}
				if(dfsnum[i] > backnum[nbr.fnum]) {
					backnum[i] = Math.min(backnum[i], backnum[nbr.fnum]);
				}
				recursed[i] = true;
			}
			nbr = nbr.next;
		}
	}
}

