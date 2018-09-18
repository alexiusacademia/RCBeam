package com.structuralengineering.rcbeam.properties;

public class BeamSectionNode {
    // Represents a node
    private double x, y;

    public BeamSectionNode(double lx, double ly) {
        this.x = lx;
        this.y = ly;
    }

    public BeamSectionNode() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
