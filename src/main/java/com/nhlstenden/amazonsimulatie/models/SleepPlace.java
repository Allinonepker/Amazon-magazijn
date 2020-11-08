package com.nhlstenden.amazonsimulatie.models;

public class SleepPlace {



    private double x;
    private double z;
    private double y;

    private boolean taken = false;

    SleepPlace(double x, double z, double y){
        this.x = x;
        this.z = z;
        this.y = y;
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


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
