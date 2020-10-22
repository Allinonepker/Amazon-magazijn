package com.nhlstenden.amazonsimulatie.models;

class node {
	
	private int X = 0;
	private int Z = 0;
	
	private int previousX = 0;
	private int previousZ = 0;
	
	private int Shortestdistance = 999;
	
	node(int X, int Z) {
		this.X = X;
		this.Z = Z;	
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