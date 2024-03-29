package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Deze class stelt een robot voor. Hij impelementeerd de class Object3D, omdat het ook een
 * 3D object is. Ook implementeerd deze class de interface Updatable. Dit is omdat
 * een robot geupdate kan worden binnen de 3D wereld om zich zo voort te bewegen.
 */
class Truck extends Object implements Object3D, Updatable, ITruck {

    private List<Position> actionlist = new ArrayList<Position>();

    private PropertyChangeSupport support;
    
    private double rotationX = 0;
    private double rotationY = Math.PI / 2;
    private double rotationZ = 0;

    private int state = 1;

    public Truck(double x, double y, double z) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
        support = new PropertyChangeSupport(this);
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
        if (!actionlist.isEmpty()) {
            Position action = actionlist.remove(0);

            this.x = action.getX();
            this.z = action.getZ();
            this.y = action.getY();

            this.rotationX = action.getrotationX();
            this.rotationZ = action.getrotationZ();
            this.rotationY = action.getrotationY();

            return true;

        } else {
            if(state == 0)
            return false;

            // hier wordt als de truck een staat hoger gaat aangegeven wat de truck moet doen
            // staat 1 is idle 
            // staat 2 is naar het dock rijden.
            // staat 3 is dozen in spawnen en taken voor de robots generen en activeren.
            // staat 4 is om de robot weer te activeren om weg te rijden.
            // staat 5 is van het dock weg rijden.
            // staat 6 is om de truck weer naar de beginstaat te brengen. 
            UpdateState(state + 1);
            if(state == 2){
                goingToDock();
            }
            if(state == 5){
                goingBack();
            }
            if(state == 6){
                state = 1;
            }
            
            return true;
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }

    // Dit is de functie die de staat van de truck update deze voert ook gelijk een propertychange naar de world zodat die ook de juiste acties uit voert. 
    public void UpdateState(int newstate){
        int oldstate = state;
        state = newstate;
        support.firePropertyChange("Truck", oldstate, newstate);
    }

    public int getState(){
        return this.state;
    }
    
    // Functie om de truck weg van het dock te laten rijden. 
    public void goingBack() {
        List<Position> positions = new ArrayList<>();
        for(double i = 0; i < 74; i++){
            positions.add(new Position(this.x + i, this.z , this.y, this.rotationX, this.rotationZ, this.rotationY));
        }
        FeedPositions(positions);	
    }
        
    // Functie om de truck naar het dock toe te laten rijden
    public void goingToDock() {
        List<Position> positions = new ArrayList<>();
        for(double i = 0; i < 74; i++){
            positions.add(new Position(this.x - i, this.z , this.y, this.rotationX, this.rotationZ, this.rotationY));
        }
        FeedPositions(positions);	
    }


    public void FeedPositions(List<Position> newactions){
        for(Position i : newactions)
        this.actionlist.add(i);
    }

    
    @Override
    public String getType() {
        /*
         * Dit onderdeel wordt gebruikt om het type van dit object als stringwaarde terug
         * te kunnen geven. Het moet een stringwaarde zijn omdat deze informatie nodig
         * is op de client, en die verstuurd moet kunnen worden naar de browser. In de
         * javascript code wordt dit dan weer verder afgehandeld.
         */
        return Truck.class.getSimpleName().toLowerCase();
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