package com.nhlstenden.amazonsimulatie.models;

import java.util.ArrayList;
import java.util.List;


class Shortestpath {
	
	public Shortestpath() {}
	
	public List<int[]> getShortestpath(int[][] layout, int startX, int startZ, int endX, int endZ){
		
		List<Node> unvisited = getnodes(layout);
		List<Node> visited = new ArrayList<Node>();
		List<int[]> shortestpath = new ArrayList<int[]>();

		
		Node currentnode = getNode(startX, startZ, unvisited);
		if(currentnode == null){
			currentnode = new Node(startX, startZ);
			unvisited.add(currentnode);
		}

		Node endnode = getNode(endX, endZ, unvisited);
		if(endnode == null){
			endnode = new Node(endX, endZ);
			unvisited.add(endnode);
		}

		currentnode.Setshortestdistance(0);
		
		
		while(!unvisited.isEmpty()) {
			currentnode = unvisited.get(0);
			for(Node i : unvisited) {
				if(i.getshortestdistance() < currentnode.getshortestdistance()) {
					currentnode = i;
				}
			}
		
			List<Node> neighbours = getNeighbours(currentnode, unvisited);
			
			int currentX = currentnode.getX();
			int currentZ = currentnode.getZ();
			
			for(Node node: neighbours) {
				unvisited.remove(node);
				node.Setpreviousnode(currentX, currentZ);
				node.Setshortestdistance(currentnode.getshortestdistance() + 1);
				unvisited.add(node);
			}
			
			visited.add(currentnode);
			unvisited.remove(currentnode);
		}
		
		Node prevnode = endnode;
		Node startnode = getNode(startX, startZ, visited);
		
		while(prevnode != startnode) {
			for(Node i : visited) {
				if(prevnode == i) {
					int prevnodeX = i.getprevX();
					int prevnodeZ = i.getprevZ();
					prevnode = getNode(prevnodeX, prevnodeZ, visited);
					int[] nodexy = {i.getX(), i.getZ()};
					shortestpath.add(0, nodexy);
				}
			}
		}
		return shortestpath;
	}

	
	
	
	private Node getNode(int X, int Z, List<Node> nodelist) {
		for(int i = 0;i < nodelist.size(); i++) {
			Node node = nodelist.get(i);
			if(node.getX() == X && node.getZ() == Z)
				return node;
		}
		return null;
	}
	
	
	private List<Node> getNeighbours(Node point, List<Node> nodelist) {
		
		int X = point.getX();
		int Z = point.getZ();
		List<Node> Neighbours = new ArrayList<Node>();
		
		for(Node i: nodelist) {
			
			int Xinspecting = i.getX();
			int Zinspecting = i.getZ();
			
			if((X == Xinspecting && (Z - 1) == Zinspecting) || (X == Xinspecting && (Z + 1) == Zinspecting) || ((X - 1) == Xinspecting && Z == Zinspecting) || ((X + 1) == Xinspecting && Z == Zinspecting)) {
				Neighbours.add(i);
			}	
		}
		return Neighbours;
	}
	
	
	private List<Node> getnodes(int[][] layout){
		
		List<Node> nodes = new ArrayList<Node>();
			
		for(int i = 0; i < 30; i++) {
			for(int j = 0; j < 30; j++) {
				if(layout[i][j] == 1 || layout[i][j] == 2) {
					Node newnode = new Node((i),j);
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


