package com.nhlstenden.amazonsimulatie.models;
import java.util.ArrayList;
import java.util.List;

public class Animator {
    
    double fpa = 10;


    public Animator(double fpa) {
        this.fpa = fpa;
    }

    List<Position> GetAnimation (List<int[]> path, Position currentposition){

        List<Position> positionlist = new ArrayList<Position>();
        double rotation = currentposition.getrotationY(); // 0.5 PI = north, PI = west, 1.5 PI = south, 0 or 2 PI = east
        double direction = (rotation/(2*Math.PI)) - Math.floor(rotation/(2*Math.PI)); // 0 = east, 0.25 is north, 0.5 is west, 0.75 south
        rotation = direction * 2 * Math.PI;

        Position lastposition = currentposition;
        Position newposition = null;



        while(!path.isEmpty()){
            int[] action = path.remove(0);

            double verticalchange = (action[0] - (lastposition.getX() - 0.5)) / fpa;
            double horizontalchange = (action[1] - (lastposition.getZ() - 0.5)) / fpa;

            double wanteddirection = 0;
            if(verticalchange > 0){
                wanteddirection = 0.25;
            }
            if(verticalchange < 0){
                wanteddirection = 0.75;
            }
            if(horizontalchange > 0){
                wanteddirection = 0;
            }
            if(horizontalchange < 0){
                wanteddirection = 0.5;
            }
            if(wanteddirection != direction){
                double rotationneeded = wanteddirection - direction;
                if(rotationneeded > 0.5){
                    rotationneeded = rotationneeded - 1;
                }
                double rotationstep = rotationneeded/fpa * 2 * Math.PI;
                for(double i = 1; i <= fpa; i++){
                    newposition = new Position(lastposition.getX(), lastposition.getZ(), lastposition.getY(), lastposition.getrotationX(), lastposition.getrotationZ(), lastposition.getrotationY() + rotationstep * i);
                    positionlist.add(newposition);
                }
                lastposition = newposition;
                direction = wanteddirection;
            }

            for(double i = 1; i <= fpa; i++){
                newposition = new Position(lastposition.getX() + verticalchange * i, lastposition.getZ() + horizontalchange * i, lastposition.getY(), lastposition.getrotationX(), lastposition.getrotationZ(), lastposition.getrotationY());
                positionlist.add(newposition);
            }
            lastposition = newposition;
        }

        return positionlist;




    }



}
