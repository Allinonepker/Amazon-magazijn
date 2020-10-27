package com.nhlstenden.amazonsimulatie.models;

class Node {
	
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