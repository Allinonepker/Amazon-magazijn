package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

public class RobotPath implements Object3D {
	
    private UUID uuid;
    
    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = Math.PI / 2;
    private double rotationY = 0;
    private double rotationZ = 0;

    public RobotPath(double x, double z, double y) {
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.z = z;
        this.y = y;
    }

    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public String getType() {

        return RobotPath.class.getSimpleName().toLowerCase();
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