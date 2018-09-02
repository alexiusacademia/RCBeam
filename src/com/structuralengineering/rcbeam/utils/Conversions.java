package com.structuralengineering.rcbeam.utils;

public final class Conversions {

  /**
   * Converts pound force per square inch (psi) to Mega Pascal (MPa)
   * @param psi Stress in psi unit.
   * @return Stress in MPa unit.
   */
  public static double PSItoMPa(double psi) {
    psi = psi / 2.204;
    psi = psi * 9.81;
    psi = psi / Math.pow(25.4, 2);
    return psi;
  }

  /**
   * Converts Mega Pascal to Pound Force per square inch.
   * @param mpa Stress in MPa unit.
   * @return Stress in PSI.
   */
  public static double MPatoPSI(double mpa) {
    mpa = mpa * 2.204;
    mpa = mpa / 9.81;
    mpa = mpa * Math.pow(25.4, 2);
    return mpa;
  }

  /**
   * Converts millimeter to inch.
   * @param mm Measurement in millimeter.
   * @return Measurement in inch/(es).
   */
  public static double mmToIn(double mm) {
    return mm / 25.4;
  }

  /**
   * Converts inches to millimeter.
   * @param in Measurement in inches.
   * @return Measurement in millimeter/(s).
   */
  public static double inTomm(double in) {
    return in * 25.4;
  }
}
