package com.nhlstenden.amazonsimulatie.models;

// Dit is de klasse die wordt gebruikt om de plekken op te slaan waar robots op kunnen rusten 
public class SleepPlace extends Object {
    
    private boolean taken = false;

    SleepPlace(double x, double z, double y){
        this.x = x;
        this.z = z;
        this.y = y;
    }

    public String getType() {

        return SleepPlace.class.getSimpleName().toLowerCase();
    }
    
    public void Usesleepplace(){
        taken = true;
    }

    public void EmptySleepplace(){
        taken = false;
    }

    public boolean getTaken() {
        return taken;
    }

}
