package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

// Dit is de klasse die wordt gebruikt om de plaatsen aan te geven waar de dozen kunnen worden opgeslagen 
public class Storageplace extends Object implements Object3D, Boxplace {

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;
    
    private boolean empty = true;
    private Box box;

    public Storageplace(double x, double z, double y) {
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

        return Storageplace.class.getSimpleName().toLowerCase();
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