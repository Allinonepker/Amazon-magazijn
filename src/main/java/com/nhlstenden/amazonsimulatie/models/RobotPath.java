package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

//Dit is de klasse voor het pad van de robot.
public class RobotPath extends Object implements Object3D {

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    public RobotPath(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }


    @Override
    public String getType() {

        return RobotPath.class.getSimpleName().toLowerCase();
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