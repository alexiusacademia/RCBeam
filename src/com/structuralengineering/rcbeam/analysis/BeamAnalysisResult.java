package com.structuralengineering.rcbeam.analysis;

public class BeamAnalysisResult {
    private double momentC;
    private double curvatureC;
    private double kd;

    /**
     * ******************************************
     * Getters
     * ******************************************
     */
    public double getMomentC() {
        return momentC;
    }

    /**
     * ******************************************
     * Setters
     * ******************************************
     */
    public void setMomentC(double momentC) {
        this.momentC = momentC;
    }

    public double getCurvatureC() {
        return curvatureC;
    }

    public void setCurvatureC(double curvatureC) {
        this.curvatureC = curvatureC;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }
}
