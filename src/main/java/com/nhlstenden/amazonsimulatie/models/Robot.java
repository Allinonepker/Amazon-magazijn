package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;
import org.javatuples.*;

/*
 * Deze class stelt een robot voor. Hij impelementeerd de class Object3D, omdat het ook een
 * 3D object is. Ook implementeerd deze class de interface Updatable. Dit is omdat
 * een robot geupdate kan worden binnen de 3D wereld om zich zo voort te bewegen.
 */
class Robot implements Object3D, Updatable {
    private UUID uuid;

    int[][] layout = new Layout().Getlayout();
    
    private Object3D task = null;
    private boolean goingBack = false;
    
    private Pair<Double, Double> start;
    private Pair<Double, Double> finish;
    
    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    public Robot(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }
    
    public Object3D getTask() {
    	return task;
    }
    
    public void giveTask(Box box) {
    	this.task = box;
    	start = new Pair<Double, Double>(x, y);
    	finish = new Pair<Double, Double>(box.getX(), box.getZ());
    	
//    	Dijkstra dijktra = new Dijkstra(layout, start, finish);
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

        	

    	
//    	System.out.println((int)this.x);
//    	
//    	for (int layx = 0; layx < layout[0].length; layx++) {
//    		for (int layz = 0; layz < layout[1].length; layz++) {
//    			System.out.println(layx + " " + layz + " " + layout[layx][layz]);
//        	}
//    	}
//    	
//    	System.out.println(layout[(int) (this.x - 1)][(int) (this.z)]);
//    	System.out.println(layout[(int) (this.x + 1)][(int) (this.z)]);
//    	System.out.println(layout[(int) (this.x)][(int) (this.z + 1)]);
//    	System.out.println(layout[(int) (this.x)][(int) (this.z - 1)]);
    	
    	

    	
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

    @Override
    public double getRotationZ() {
        return this.rotationZ;
    }
}