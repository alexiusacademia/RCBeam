package com.structuralengineering.rcbeam.utils;

import com.structuralengineering.rcbeam.properties.Unit;

public final class Conversions {

    /**
     * Converts pound force per square inch (psi) to Mega Pascal (MPa)
     *
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
     *
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
     *
     * @param mm Measurement in millimeter.
     * @return Measurement in inch/(es).
     */
    public static double mmToIn(double mm) {
        return mm / 25.4;
    }

    /**
     * Converts inches to millimeter.
     *
     * @param in Measurement in inches.
     * @return Measurement in millimeter/(s).
     */
    public static double inTomm(double in) {
        return in * 25.4;
    }


    /**
     * Converts the linear dimension
     *
     * @param distance dimension
     * @param u unit
     * @return linearConverted dimension in metric
     */
    public static double linearConverted(double distance, Unit u) {
        if (u == Unit.ENGLISH) {
            return mmToIn(distance);
        } else {
            return distance;
        }
    }

    /**
     * Converts pressure
     * @param pressure pressure
     * @param u unit
     * @return converted oressure in MPa
     */
    public static double pressureConverted(double pressure, Unit u) {
        if (u == Unit.ENGLISH) {
            return MPatoPSI(pressure);
        } else {
            return pressure;
        }
    }

    /**
     * Converts square millimeters to square inches
     * @param area Area in square millimeters
     * @return area in square inches
     */
    public static double toSquareInches(double area) {
        return area / Math.pow(25.4, 2);
    }

    /**
     * Converts square inches to square millimeters
     * @param area Area in square inches.
     * @return Area in square millimeters.
     */
    public static double toSquareMillimeters(double area) {
        return area * Math.pow(25.4, 2);
    }

    /**
     * Returns moment in lbs-ft
     * @param momentInMetric
     * @return
     */
    public static double toEnglishMoment(double momentInMetric) {
        double m = 0;

        m = momentInMetric * 2.204;
        m = m / 9.81;
        m = m / 25.4;
        m = m / 12;

        return m;
    }
}
