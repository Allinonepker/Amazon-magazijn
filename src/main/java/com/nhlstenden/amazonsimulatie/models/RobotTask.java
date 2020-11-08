package com.nhlstenden.amazonsimulatie.models;

// Dit is de klasse voor een taak van een robot, deze klasse wordt gebruikt om taak op te slaan en aan een robot te geven zo weten de robots wat ze moeten doen. 
public class RobotTask {
    
    private Box box;
    private boolean newbox;

    RobotTask(Box box, boolean newbox){

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
