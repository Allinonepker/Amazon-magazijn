package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

public class Storageplace implements Object3D, Boxplace {
	
    private UUID uuid;
    
    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;
    
    private boolean empty = true;

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
    public void FillPlace(){
        this.empty = false;
    }

    @Override
    public void EmptyPlace(){
        this.empty = true;
    }


    

    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public String getType() {

        return Storageplace.class.getSimpleName().toLowerCase();
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