package com.structuralengineering.rcbeam.analysis;

public class BeamAnalysisResult {
  private double momentC;
  private double curvatureC;
  private double kd;

  /** *******************************************
   * Getters
   ******************************************* */
  public double getMomentC() {
    return momentC;
  }

  public double getCurvatureC() {
    return curvatureC;
  }

  public double getKd() {
    return kd;
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

  public void setKd(double kd) {
    this.kd = kd;
  }
}
