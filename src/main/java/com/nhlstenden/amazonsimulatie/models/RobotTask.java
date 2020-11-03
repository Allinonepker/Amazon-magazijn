package com.nhlstenden.amazonsimulatie.models;

public class RobotTask {
    
    private Box box;
    private boolean newbox;

    RobotTask (Box box, boolean newbox){

        this.box = box;
        this.newbox = newbox;

    }

    public boolean IsNewBox(){
        return this.newbox;
    }

    public Box Getbox (){
        return this.box;
    }

}
