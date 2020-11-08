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
class Robot extends Object implements Object3D, Updatable, IRobot {


    private List<Position> actionlist = new ArrayList<Position>();

    private PropertyChangeSupport support;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    // Staat van de robot
    // 0 is slapen 
    // 1 is idle
    // 2 is starten nieuwe taak, naar doos lopen
    // 3 is oppakken doos, naar dock lopen 
    // 4 is doos neerzetten
    // 5 is kijken naar nieuwe taak
    private int state = 4;
    private RobotTask task;

    private SleepPlace sleepplace;

    public Robot(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
        // hier wordt een propertychangesupport aangemaakt zodat de robot kan communiceren met de wereld 
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /*
     * Deze update methode wordt door de World aangeroepen wanneer de World zelf
     * geupdate wordt. Dit betekent dat elk object, ook deze robot, in de 3D wereld
     * steeds een beetje tijd krijgt om een update uit te voeren. In de
     * updatemethode hieronder schrijf je dus de code die de robot steeds uitvoert
     * (bijvoorbeeld positieveranderingen). Wanneer de methode true teruggeeft
     * (zoals in het voorbeeld), betekent dit dat er inderdaad iets veranderd is en
     * dat deze nieuwe informatie naar de views moet worden gestuurd. Wordt false
     * teruggegeven, dan betekent dit dat er niks is veranderd, en de informatie
     * hoeft dus niet naar de views te worden gestuurd. (Omdat de informatie niet
     * veranderd is, is deze dus ook nog steeds hetzelfde als in de view)
     */
    @Override
    public boolean update() {
         // Loopt de actielijst af zodat een voor een de posities worden aangenomen zodat het lijkt alsof de robot beweegt. 
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
        // Als de actielijst leeg is is de robot klaar met zijn animatie de robot wordt dan in de volgende staat gebracht 
        else {
            // Als hij in rust stand staat wordt er niks gedaan. 
            if(state == 0)
            return true;

            UpdateState(state + 1);
            return true;
        }
    }


    public void setSleepplace(SleepPlace sleepplace) {
        this.sleepplace = sleepplace;
    }
    
    public SleepPlace getSleepplace() {
        return sleepplace;
    }

    
    public void StopActions(){
        actionlist.clear();
    }

    // Zorgt er voor dat de robot in de volgende staat wordt gebracht deze verandering wordt ook naar de wereld verstuurt zodat de volgde acties worden uitgevoerd
    public void UpdateState(int newstate){
        int oldstate = state;
        state = newstate;
        support.firePropertyChange("Robot", oldstate, newstate);
    }
    
    public int getState(){
        return this.state;
    }

    public Position getPosition(){
        Position position = new Position(this.x, this.z, this.y, this.rotationX, this.rotationZ, this.rotationY);
        return position;
    }

    public void SetTask(RobotTask task){
        this.task = task;
    }

    public RobotTask GetTask(){
        return this.task;
    }

    // Zorgt ervoor zodat een animatie kan worden verstuurd naar de actielijst van de robot, zodat de robot een animatie kan uitvoeren.
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
        return Robot.class.getSimpleName().toLowerCase();
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