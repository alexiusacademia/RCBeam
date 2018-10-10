package com.structuralengineering.rcbeam.properties;

public class Node {
    // Represents a node
    private double x, y;

    public Node(double lx, double ly) {
        this.x = lx;
        this.y = ly;
    }

    public Node() {

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
