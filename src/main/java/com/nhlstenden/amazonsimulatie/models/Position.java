package com.nhlstenden.amazonsimulatie.models;

// Dit is de positie klasse, deze wordt gebruikt voor het opslaan van de animaties maar ook om posities van de objecten in door te geven
public class Position {

    private double x;
    private double z;
    private double y;
    
    private double xr;
    private double zr;
    private double yr;

    Position(double x, double z, double y, double xr, double zr, double yr){
        this.x = x;
        this.z = z;
        this.y = y;
        this.xr = xr;
        this.zr = zr;
        this.yr = yr;
    }

	public double getX() {
        return x;
    }
    public double getZ() {
        return z;
    }
    public double getY() {
        return y;
    }

    public double getrotationX() {
        return xr;
    }
    public double getrotationZ() {
        return zr;
    }
    public double getrotationY() {
        return yr;
    }
    
}
