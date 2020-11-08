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
        // Haalt de huidige rotatie op
        double rotation = currentposition.getrotationY(); 

        // 0.5 PI = north, PI = west, 1.5 PI = south, 0 or 2 PI = east
        // 0 = east, 0.25 is north, 0.5 is west, 0.75 south
        double direction = (rotation / (2 * Math.PI)) - Math.floor(rotation / (2 * Math.PI)); 
        rotation = direction * 2 * Math.PI;

        Position lastposition = currentposition;
        Position newposition = null;

        
        while (!path.isEmpty()) {
            int[] action = path.remove(0);

            double verticalchange = (action[0] - (lastposition.getX())) / fpa;
            double horizontalchange = (action[1] - (lastposition.getZ())) / fpa;

            double wanteddirection = 0;
            if (verticalchange > 0 || verticalchange < 0) {
                wanteddirection = 0.25;
            }
            if (horizontalchange > 0 || horizontalchange < 0) {
                wanteddirection = 0;
            }
            if (wanteddirection != direction) {

                double rotationneeded = wanteddirection - direction;
                if (rotationneeded > 0.5) {
                    rotationneeded = rotationneeded - 1;
                }
                double rotationstep = rotationneeded / fpa * 2 * Math.PI;
                for (double i = 1; i <= fpa; i++) {
                    newposition = new Position(lastposition.getX(), lastposition.getZ(), lastposition.getY(),
                            lastposition.getrotationX(), lastposition.getrotationZ(),
                            lastposition.getrotationY() + rotationstep * i);
                    positionlist.add(newposition);
                }
                lastposition = newposition;
                direction = wanteddirection;
            }

            for (double i = 1; i <= fpa; i++) {
                newposition = new Position(lastposition.getX() + verticalchange * i,
                        lastposition.getZ() + horizontalchange * i, lastposition.getY(), lastposition.getrotationX(),
                        lastposition.getrotationZ(), lastposition.getrotationY());
                positionlist.add(newposition);
            }
            lastposition = newposition;
        }

        return positionlist;

    }

}
