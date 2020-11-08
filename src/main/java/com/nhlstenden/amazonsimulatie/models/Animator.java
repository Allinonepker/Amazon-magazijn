package com.nhlstenden.amazonsimulatie.models;

import java.util.ArrayList;
import java.util.List;

public class Animator {

    double fpa = 10;

    // Dit is de constructor voor de animator, hier wordt een variable mee geven die
    // bepaald hoeveel frames er moeten worden genereerd tussen een stap, zogenaamde
    // frames per action.
    public Animator(double fpa) {
        this.fpa = fpa;
    }

    // Dit is de functie van de animator die de animatie genereerd voor een robot over een pad. 
    List<Position> GetAnimation(List<int[]> path, Position currentposition) {


        // Maakt een lijst aan met posities voor de animatie.
        List<Position> positionlist = new ArrayList<>();

        // Haalt de huidige rotatie op van het object
        double rotation = currentposition.getrotationY(); 

        // Dit zorgt er voor dat de richting van de robot opgelaad wordt zonder extra rotaties.
        
        // 0.5 PI = north, PI = west, 1.5 PI = south, 0 or 2 PI = east
        // 0 = east, 0.25 is north, 0.5 is west, 0.75 south

        double direction = (rotation / (2 * Math.PI)) - Math.floor(rotation / (2 * Math.PI)); 
        rotation = direction * 2 * Math.PI;

        Position lastposition = currentposition;
        Position newposition = null;

        // Lust net zo lang door totdat het hele pad is afgelopen. 
        while (!path.isEmpty()) {
            int[] action = path.remove(0);

            // rekent uit hoe groot de stap in elke richting moet zijn tussen de frames in.
            double verticalchange = (action[0] - (lastposition.getX())) / fpa;
            double horizontalchange = (action[1] - (lastposition.getZ())) / fpa;

            double wanteddirection = 0;

            // bepaald welke kant de robot op moet staan om de stap te verzetten.
            if (verticalchange > 0 || verticalchange < 0) {
                wanteddirection = 0.25;
            }
            if (horizontalchange > 0 || horizontalchange < 0) {
                wanteddirection = 0;
            }
            // Als de robot nog niet goed gedraaid is wordt er een animatie toegevoegd om de robot te draaien.
            if (wanteddirection != direction) {

                // Hier wordt de benodigde rotatie berekend. 
                double rotationneeded = wanteddirection - direction;
                if (rotationneeded > 0.5) {
                    rotationneeded = rotationneeded - 1;
                }
                double rotationstep = rotationneeded / fpa * 2 * Math.PI;
                // Hier worden de posities toegevoegd voor het draaien. 
                for (double i = 1; i <= fpa; i++) {
                    newposition = new Position(lastposition.getX(), lastposition.getZ(), lastposition.getY(),
                            lastposition.getrotationX(), lastposition.getrotationZ(),
                            lastposition.getrotationY() + rotationstep * i);
                    positionlist.add(newposition);
                }
                // Hier worden de waardes veranderd zodat de robot bij de volgende stap weer verder gaat bij waar die gestopt is 
                lastposition = newposition;
                direction = wanteddirection;
            }

            // Hier worden de posities toegevoegd om een stap te zetten 
            for (double i = 1; i <= fpa; i++) {
                newposition = new Position(lastposition.getX() + verticalchange * i,
                        lastposition.getZ() + horizontalchange * i, lastposition.getY(), lastposition.getrotationX(),
                        lastposition.getrotationZ(), lastposition.getrotationY());
                positionlist.add(newposition);
            }
            // Hier wordt de waarde weer aangepast zodat hij weer op het juiste punt verder gaat.
            lastposition = newposition;
        }

        // Stuurt de posities terug zodat ze door een robot gebruikt kunnen worden.
        return positionlist;

    }

}
