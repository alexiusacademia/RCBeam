package com.structuralengineering.rcbeam;

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

  public double getY() {
    return y;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }
}
