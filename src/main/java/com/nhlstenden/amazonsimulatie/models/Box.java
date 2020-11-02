package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

class Box implements Object3D, Updatable {
	private UUID uuid;

	private boolean toTruck;
	
    private double x = 0;
    private double y = 0;
    private double z = 0;
    
    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    private boolean taken = false;
    
    public Box(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
        this.toTruck = true;
    }
    
    public boolean getTaken() {
    	return taken;
    }
    
    public void giveTaken(boolean taken) {
    	this.taken = taken;
    }
    
    public boolean getToTruck() {
    	return toTruck;	
    }
    
    public void setToTruck(boolean toTruck) {
    	this.toTruck = toTruck;
    }
    
    @Override
    public boolean update() {
    	
//    	if (this.x != this.startX && this.z != this.startZ) {
//        	this.x += (this.startX - this.x) / 20;
//        	this.z += (this.startZ - this.z) / 20;
//        	if (Math.abs(this.startX - this.x) < 2 || Math.abs(this.startZ - this.y) < 2) {
//        		this.x += (this.startX - this.x);
//        		this.z += (this.startZ - this.z);
//        	}
        	return true;
    }

    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public String getType() {
        /*
         * Dit onderdeel wordt gebruikt om het type van dit object als stringwaarde terug
         * te kunnen geven. Het moet een stringwaarde zijn omdat deze informatie nodig
         * is op de client, en die verstuurd moet kunnen worden naar de browser. In de
         * javascript code wordt dit dan weer verder afgehandeld.
         */
        return Box.class.getSimpleName().toLowerCase();
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public double getRotationX() {
        return this.rotationX;
    }

    @Override
    public double getRotationY() {
        return this.rotationY;
    }

    @Override
    public double getRotationZ() {
        return this.rotationZ;
    }
}
