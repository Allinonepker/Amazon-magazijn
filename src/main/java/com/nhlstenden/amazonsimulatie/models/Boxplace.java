package com.nhlstenden.amazonsimulatie.models;

// Dit is de interface voor een plek waar een doos kan staan, deze wordt gebruikt in het dock en storageplace. 
// Hierdoor kan ik bijde klassen opslaan in 1 lijst wat het makkelijk maakt om de functies voor de robot te schrijven
public interface Boxplace {

    public abstract boolean IsEmpty();

    public abstract void FillPlace(Box box);

    public abstract void EmptyPlace();

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract Box GetBox();
}
