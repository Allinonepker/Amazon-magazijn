package com.nhlstenden.amazonsimulatie.models;

import java.util.ArrayList;
import java.util.List;


class shortestpath {
	
	public shortestpath() {}
	
	public List<int[]> getShortestpath(int[][] layout, int startX, int startZ, int endX, int endZ){
		
		List<node> nodelist = getnodes(layout);
		List<node> unvisited = nodelist;
		List<node> visited = new ArrayList<node>();
		List<int[]> shortestpath = new ArrayList<int[]>();

		
		node currentnode = getNode(startX, startZ, nodelist);
		node endnode = getNode(endX, endZ, nodelist);
		
		currentnode.Setshortestdistance(0);
		
		
		while(!unvisited.isEmpty()) {
			for(node i : unvisited) {
				if(i.getshortestdistance() < currentnode.getshortestdistance()) {
					currentnode = i;
				}
			}
		
			List<node> neighbours = getNeighbours(currentnode, unvisited);
			
			int currentX = currentnode.getX();
			int currentZ = currentnode.getZ();
			
			for(node node: neighbours) {
				unvisited.remove(node);
				node.Setpreviousnode(currentX, currentZ);
				node.Setshortestdistance(currentnode.getshortestdistance() + 1);
				unvisited.add(node);
			}
			
			visited.add(currentnode);
			unvisited.remove(currentnode);
			currentnode.Setshortestdistance(Integer.MAX_VALUE);
		}
		
		node prevnode = endnode;
		while(prevnode == getNode(startX, startZ, visited)) {
			for(node i : visited) {
				if(prevnode == i) {
					int prevnodeX = i.getprevX();
					int prevnodeZ = i.getprevZ();
					prevnode = getNode(prevnodeX, prevnodeZ, visited);
					int[] nodexy = {prevnodeX, prevnodeZ};
					shortestpath.add(0, nodexy);
				}
			}
		}
		return shortestpath;
	}
	
	
	private node getNode(int X, int Z, List<node> nodelist) {
		for(int i = 0;i < nodelist.size(); i++) {
			node node = nodelist.get(i);
			if(node.getX() == X && node.getZ() == Z)
				return node;
		}
		return null;
	}
	
	
	private List<node> getNeighbours(node point, List<node> nodelist) {
		
		int X = point.getX();
		int Z = point.getZ();
		List<node> Neighbours = new ArrayList<node>();
		
		for(node i: nodelist) {
			
			int Xinspecting = point.getX();
			int Zinspecting = point.getZ();
			
			if((X == Xinspecting && (Z - 1) == Zinspecting) || (X == Xinspecting && (Z + 1) == Zinspecting) || ((X - 1) == Xinspecting && Z == Zinspecting) || ((X + 1) == Xinspecting && Z == Zinspecting)) {
				Neighbours.add(i);
			}	
		}
		return Neighbours;
	}
	
	
	private List<node> getnodes(int[][] layout){
		
		List<node> nodes = new ArrayList<node>();
			
		for(int i = 0; i < 30; i++) {
			for(int j = 0; j <30; i++) {
				if(layout[i][j] == 1) {
					node newnode = new node(i,j);
					nodes.add(newnode);
				}
			}
		}
		return nodes;
	}
}


