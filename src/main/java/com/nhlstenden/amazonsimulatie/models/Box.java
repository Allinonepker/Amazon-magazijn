package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

class Box implements Object3D, Updatable {
	private UUID uuid;

    private double x = 0;
    private double y = -1.0;
    private double z = 0;
    
    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    private boolean queued = false;
    private boolean taken = false;
    
    public Box(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }
    
    public boolean getTaken() {
    	return taken;
    }
    
    public void setQueued(boolean queued) {
    	this.queued = queued;
    }
    
    public boolean getQueued() {
    	return queued;
    }
    public void giveTaken(boolean taken) {
    	this.taken = taken;
    }
    
    @Override
    public boolean update() {

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
