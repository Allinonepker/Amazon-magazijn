package com.nhlstenden.amazonsimulatie.models;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Box implements Object3D, Updatable {
	private UUID uuid;

    private double x = 0;
    private double y = 0;
    private double z = 0;
    
    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    private List<Position> actionlist = new ArrayList<>();

    private boolean taken = false;
    private boolean newbox = true;

    
    public Box(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }
    
    public boolean getTaken() {
    	return taken;
    }
    
    public void giveTaken(boolean taken) {
    	this.taken = taken;
    }



    
    @Override
    public boolean update() {
        if (!actionlist.isEmpty()) {
            Position action = actionlist.remove(0);

            this.x = action.getX();
            this.z = action.getZ();
            this.y = action.getY();

            this.rotationX = action.getrotationX();
            this.rotationZ = action.getrotationZ();
            this.rotationY = action.getrotationY();

    
            return true;
        }
    return false;
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

    public void FeedPositions(List<Position> newpositions){
        for(Position i : newpositions)
        this.actionlist.add(i);
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
