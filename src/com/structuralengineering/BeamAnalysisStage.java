package com.structuralengineering;

public class BeamAnalysisStage {
  private double momentC;
  private double curvatureC;

  /** *******************************************
   * Getters
   ******************************************* */
  public double getMomentC() {
    return momentC;
  }

  public double getCurvatureC() {
    return curvatureC;
  }

  /** *******************************************
   * Setters
   ******************************************* */
  public void setMomentC(double momentC) {
    this.momentC = momentC;
  }

  public void setCurvatureC(double curvatureC) {
    this.curvatureC = curvatureC;
  }
}
