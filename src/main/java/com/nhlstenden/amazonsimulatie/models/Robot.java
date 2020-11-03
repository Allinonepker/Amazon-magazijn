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
class Robot implements Object3D, Updatable {
    private UUID uuid;

    private List<Position> actionlist = new ArrayList<Position>();

    private PropertyChangeSupport support;

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    private int state = 1;
    private RobotTask task;

    public Robot(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
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

            UpdateState(state + 1);
            return true;
        }
    }
    public void UpdateState(int newstate){
        int oldstate = state;
        state = newstate;
        support.firePropertyChange("Robot", oldstate, newstate);
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

    public void FeedPositions(List<Position> newactions){
        for(Position i : newactions)
        this.actionlist.add(i);
    }
    
    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    public void ChangeState(int i){
        this.state = i; // 0 is sleep, 1 is going to storageplace, 2 is going to dock, evt 3 is going to sleepplace
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