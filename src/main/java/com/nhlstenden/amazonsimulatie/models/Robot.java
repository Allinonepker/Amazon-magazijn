package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

/*
 * Deze class stelt een robot voor. Hij impelementeerd de class Object3D, omdat het ook een
 * 3D object is. Ook implementeerd deze class de interface Updatable. Dit is omdat
 * een robot geupdate kan worden binnen de 3D wereld om zich zo voort te bewegen.
 */
class Robot implements Object3D, Updatable {
    private UUID uuid;
    
    // taak van de robot
    private Object3D task = null;
    
    private double x = 0;
    private double y = 0.15;
    private double z = 0;

    private double startX = 0;
    private double startY = 0;
    private double startZ = 0;
    
    private boolean goingBack = false;
    
    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    public Robot(Double x, Double y, Double z) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        
        this.startX = x;
        this.startZ = z;
    }

    /*
     * Deze update methode wordt door de World aangeroepen wanneer de
     * World zelf geupdate wordt. Dit betekent dat elk object, ook deze
     * robot, in de 3D wereld steeds een beetje tijd krijgt om een update
     * uit te voeren. In de updatemethode hieronder schrijf je dus de code
     * die de robot steeds uitvoert (bijvoorbeeld positieveranderingen). Wanneer
     * de methode true teruggeeft (zoals in het voorbeeld), betekent dit dat
     * er inderdaad iets veranderd is en dat deze nieuwe informatie naar de views
     * moet worden gestuurd. Wordt false teruggegeven, dan betekent dit dat er niks
     * is veranderd, en de informatie hoeft dus niet naar de views te worden gestuurd.
     * (Omdat de informatie niet veranderd is, is deze dus ook nog steeds hetzelfde als
     * in de view)
     */
    @Override
    public boolean update() {
    	if (this.goingBack == true) {
    		this.x += (this.startX - this.x) / 20;
    		this.z += (this.startZ - this.z) / 20;
        	if (this.startX - this.x < 2 || this.startZ - this.y < 2) {
        		this.x += (this.startX - this.x);
        		this.z += (this.startZ - this.z);
        	}
    	}
    	
    	if (task != null) {
    		Double boxX = task.getX();
    		Double boxY = task.getY();
    		Double boxZ = task.getZ();
    		
    		this.x += (boxX - this.x) / 20;
    		this.z += (boxZ - this.z) / 20;
    		
        	if (Math.abs(boxX - this.x) < 2 || Math.abs(boxZ - this.y) < 2) {
        		this.x += (boxX - this.x);
        		this.z += (boxZ - this.z);
        	}
    		
    		if (boxX == this.x && boxZ == this.z) {
    			task = null;
    			this.goingBack = true;
    		}
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
        return Robot.class.getSimpleName().toLowerCase();
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

    public void giveTask(Object3D box) {
    	this.task = box;
    }
    
    public Object3D getTask() {
    	return this.task;
    }
    
    @Override
    public double getRotationZ() {
        return this.rotationZ;
    }
}