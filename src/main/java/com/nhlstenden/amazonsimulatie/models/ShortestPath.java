package com.nhlstenden.amazonsimulatie.models;

import java.util.ArrayList;
import java.util.List;

// Dit is de klasse voor het genereren van een pad voor een robot, hier wordt gebruik gemaakt van het dijkstra algorithme. 
class Shortestpath {

	public Shortestpath() {
	}

	// Dit is de constructor voor shortestpath, hier wordt de begin coördinaten,
	// eindcoördinaten en de layout mee gestuurd , hieruit stuurt hij een lijst
	// terug
	// met posities om zo snel mogelijk bij het eindpunt te komen
	public List<int[]> getShortestpath(int[][] layout, int startX, int startZ, int endX, int endZ) {

		// Hier wordt een lijst aangemaakt met zogenaamde nodes dit zijn alle punten
		// waar de robot over kan lopen deze zijn nu nog unvisited maar naarmate de code
		// vaker doorlopen wordt worden ze verplaatst naar de visited lijst.
		List<Node> unvisited = getnodes(layout);
		List<Node> visited = new ArrayList<>();
		// Hier komt de uiteindelijke lijst in te staan met het korste pad. 
		List<int[]> shortestpath = new ArrayList<>();

		// De begin nodes en eind nodes worden ook toegevoegd dit zijn de punten waar
		// meestal de doos op staat of waar de robot de doos moet afleveren, deze punten
		// mogen anders niet mee doen omdat de robots dan over de dozen of opslagpunten
		// gaan lopen.
		Node currentnode = getNode(startX, startZ, unvisited);
		if (currentnode == null) {
			currentnode = new Node(startX, startZ);
			unvisited.add(currentnode);
		}
		Node endnode = getNode(endX, endZ, unvisited);
		if (endnode == null) {
			endnode = new Node(endX, endZ);
			unvisited.add(endnode);
		}

		// Van de startnode wordt bepaald dat de afstand 0 is omdat je daar al bent. 
		currentnode.Setshortestdistance(0);

		// Zolang alle punten nog niet bekeken zijn worden er steeds punten onderzocht. 
		while (!unvisited.isEmpty()) {

			// Hier wordt het punt gezocht waarvan de kortste afstand het kleinst is en het punt nog niet is onderzocht.
			currentnode = unvisited.get(0);
			for (Node i : unvisited) {
				if (i.getshortestdistance() < currentnode.getshortestdistance()) {
					currentnode = i;
				}
			}

			// Hier wordt een nieuwe lijst aangemaakt waarin alle buren van een punt staan die nog niet onderzocht zijn, deze worden opgezocht met de functie getNeigbours. 
			List<Node> neighbours = getNeighbours(currentnode, unvisited);

			int currentX = currentnode.getX();
			int currentZ = currentnode.getZ();

			// Hier wordt de afstand van de buren bepaald die nog niet onderzocht zijn deze is zijn eigen waarde + 1.
			for (Node node : neighbours) {
				node.Setpreviousnode(currentX, currentZ);
				node.Setshortestdistance(currentnode.getshortestdistance() + 1);
			}

			visited.add(currentnode);
			unvisited.remove(currentnode);
		}

		Node prevnode = endnode;
		Node startnode = getNode(startX, startZ, visited);

		while (prevnode != startnode) {
			for (Node i : visited) {
				if (prevnode == i) {
					int prevnodeX = i.getprevX();
					int prevnodeZ = i.getprevZ();
					prevnode = getNode(prevnodeX, prevnodeZ, visited);
					int[] nodexy = { i.getX(), i.getZ() };
					shortestpath.add(0, nodexy);
				}
			}
		}
		return shortestpath;
	}

	private Node getNode(int X, int Z, List<Node> nodelist) {
		for (int i = 0; i < nodelist.size(); i++) {
			Node node = nodelist.get(i);
			if (node.getX() == X && node.getZ() == Z)
				return node;
		}
		return null;
	}

	private List<Node> getNeighbours(Node point, List<Node> nodelist) {

		int X = point.getX();
		int Z = point.getZ();
		List<Node> Neighbours = new ArrayList<Node>();

		for (Node i : nodelist) {

			int Xinspecting = i.getX();
			int Zinspecting = i.getZ();

			if ((X == Xinspecting && (Z - 1) == Zinspecting) || (X == Xinspecting && (Z + 1) == Zinspecting)
					|| ((X - 1) == Xinspecting && Z == Zinspecting) || ((X + 1) == Xinspecting && Z == Zinspecting)) {
				Neighbours.add(i);
			}
		}
		return Neighbours;
	}

	private List<Node> getnodes(int[][] layout) {

		List<Node> nodes = new ArrayList<Node>();

		for (int i = 0; i < layout[0].length; i++) {
			for (int j = 0; j < layout.length; j++) {
				if (layout[i][j] == 1 || layout[i][j] == 2 || layout[i][j] == 6) {
					Node newnode = new Node((i), j);
					nodes.add(newnode);
				}
			}
		}
		return nodes;

	}

	private class Node {

		private int X;
		private int Z;

		private int previousX;
		private int previousZ;

		private int Shortestdistance = Integer.MAX_VALUE;

		public Node(int x, int z) {
			this.X = x;
			this.Z = z;
		}

		public int getX() {
			return X;
		}

		public int getZ() {
			return Z;
		}

		public int getprevX() {
			return previousX;
		}

		public int getprevZ() {
			return previousZ;
		}

		public int getshortestdistance() {
			return Shortestdistance;
		}

		public void Setshortestdistance(int distance) {
			Shortestdistance = distance;
		}

		public void Setpreviousnode(int previousX, int previousZ) {
			this.previousX = previousX;
			this.previousZ = previousZ;
		}

	}
}
