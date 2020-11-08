package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

//m.b.v. van deze klasse worden de basisvariabelen en getters ervan overgeërfd 
public abstract class Object {
    
    protected UUID uuid;
    
    protected double x = 0;
    protected double y = 0;
    protected double z = 0;
    
    public abstract String getType();

    public String getUUID() {
        return this.uuid.toString();
    }
    
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
