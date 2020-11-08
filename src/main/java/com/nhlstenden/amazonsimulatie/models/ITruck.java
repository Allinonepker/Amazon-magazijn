package com.nhlstenden.amazonsimulatie.models;

public interface ITruck {
    // Dit is de functie die de staat van de truck update deze voert ook gelijk een propertychange naar de world zodat die ook de juiste acties uit voert. 
    public void UpdateState(int newstate);
    
    public int getState();
    
    // Functie om de truck weg van het dock te laten rijden. 
    public void goingBack();
     
    // Functie om de truck naar het dock toe te laten rijden
    public void goingToDock();
}
