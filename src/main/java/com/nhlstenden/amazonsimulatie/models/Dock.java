package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

// Dit is de klasse voor een plaats in de truck, deze wordt gebruikt om aan tegeven of er een doos op staat en welke doos er op staat. 
public class Dock extends Object implements Object3D , Boxplace{
	


    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;
    
    private boolean empty = true;
    private Box box;

    public Dock(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }

    @Override
    public boolean IsEmpty(){
        return this.empty;
    }

    @Override
    public void FillPlace(Box box){
        this.empty = false;
        this.box = box;
    }

    @Override
    public void EmptyPlace(){
        this.empty = true;
        this.box = null;
    }

    @Override
    public Box GetBox(){
        return box;
    }

    @Override
    public String getType() {

        return Dock.class.getSimpleName().toLowerCase();
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