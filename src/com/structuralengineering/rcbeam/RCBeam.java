package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;

import java.io.Serializable;

public class RCBeam implements Serializable {
  /**
   * Strains
   */
  private double strainConcreteCompression;
  private double strainConcreteTension;
  private double strainSteelCompression;
  private double strainSteelTension;

  /**
   * Limits
   */
  private double strainConcreteLimit;

  /**
   * Beam section, defined by nodes
   */
  private BeamSection beamSection;

  /**
   * Beam properties
   */
  private double fcPrime = 0;                 // Concrete yield strength MPa
  private double fy = 0;                      // Steel yield strength MPa
  private static final double Es = 200000;    // Steel modulus of elasticity MPa
  private double Ec;                          // Concrete secant modulus MPa
  private double n;                           // Modular ration Es/Ec

  /**
   * Results variables
   */
  private String errMessage;
  private boolean isSuccess;


  /**
   * ******************************************
   * Getters
   * ******************************************
   */
  public BeamSection getBeamSection() {
    return beamSection;
  }

  public double getStrainConcreteLimit() {
    return strainConcreteLimit;
  }

  public double getFcPrime() {
    return fcPrime;
  }

  public double getFy() {
    return fy;
  }

  public double getEc() {
    return Ec;
  }

  public double getN() {
    return n;
  }

  public String getErrMessage() {
    return errMessage;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  /**
   * ******************************************
   * Setters
   * ******************************************
   */
  public void setBeamSection(BeamSection beamSection) {
    this.beamSection = beamSection;
  }

  public void setFcPrime(double fcPrime) {
    this.fcPrime = fcPrime;
  }

  public void setFy(double fy) {
    this.fy = fy;
  }



  public void setStrainConcreteLimit(double strainConcreteLimit) {
    this.strainConcreteLimit = strainConcreteLimit;
  }

  /**
   * ******************************************
   * Methods
   * ******************************************
   */

  // Calculate moment of inertia of the whole section before and after the crack
  // at tension fiber of concrete
  private void calculateMomentsOfInertias() {
    // Check for incomplete data
    if (fcPrime == 0.0) {
      isSuccess = false;
      errMessage = "Concrete yield strength is unspecified or with value zero.";
      try {
        throw new Exception("Concrete strength not specified.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Concrete secant modulus
    Ec = 4700 * Math.sqrt(fcPrime);

    // Modular ratio
    n = Es / Ec;
  }



}
