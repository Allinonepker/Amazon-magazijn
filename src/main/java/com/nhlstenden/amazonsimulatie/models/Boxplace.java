package com.nhlstenden.amazonsimulatie.models;


public interface Boxplace {
    	

    public abstract boolean IsEmpty();
    public abstract void FillPlace(Box box);
    public abstract void EmptyPlace();

    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();

    public abstract Box GetBox();
}
