package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Conversions;

public class SteelCompression {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    private double totalArea;                           // Total area of steel. Default is in square millimeters
    private double fs;                                  // Actual tensile stress in steel
    private double dPrime;                              // Distance of compression steel to concrete
                                                        // extreme compression fiber in mm. (d')

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Getters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Gets the distance between compression steel centroid to concrete
     * extreme compression fiber.
     * @param u Unit
     * @return d'
     */
    public double getdPrime(Unit u) {
        if (u == Unit.ENGLISH) {
            return Conversions.mmToIn(this.dPrime);
        } else {
            return dPrime;
        }
    }

    /**
     * Gets the total area in steel in specified format.
     * If inMetric is true, in square millimeters.
     * If inMetric is false, in square inches.
     * @param u Unit.
     * @return Steel area.
     */
    public double getTotalArea(Unit u) {
        if (u == Unit.ENGLISH) {
            return Conversions.toSquareInches(this.totalArea);
        } else {
            return this.totalArea;
        }
    }

    /**
     * Returns the stess in the unit that is specified.
     * If inMetric is true, returns unit in MPa.
     * If inMetric is false, returns unit in PSI.
     * @param u Unit
     * @return fs (MPa/PSI)
     */
    public double getFs(Unit u) {
        if (u == Unit.ENGLISH) {
            return Conversions.MPatoPSI(this.fs);
        } else {
            return this.fs;
        }
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Sets the d'.
     * @param dPrime Distance from compression steel to extreme compression fiber of concrete.
     * @param u Unit
     */
    public void setdPrime(double dPrime, Unit u) {
        if (u == Unit.ENGLISH) {
            this.dPrime = Conversions.inTomm(dPrime);
        } else {
            this.dPrime = dPrime;
        }
    }

    /**
     * Sets the total area of steel in specified unit.
     * If inMetric is true, in square millimeter.
     * If inMetric is false, in square inches.
     * @param totalArea Area of steel.
     * @param u Unit
     */
    public void setTotalArea(double totalArea, Unit u) {
        if (u == Unit.ENGLISH) {
            this.totalArea = Conversions.toSquareMillimeters(totalArea);
        } else {
            this.totalArea = totalArea;
        }
    }

    /**
     * Sets the stress (fs) in the unit specified.
     * if inMetric is true, fs should be in MPa.
     * if inMetric is false, fs should be in PSI.
     * @param fs steel stress
     * @param u Unit
     */
    public void setFs(double fs, Unit u) {
        if (u == Unit.ENGLISH) {
            this.fs = Conversions.PSItoMPa(fs);
        } else {
            this.fs = fs;
        }
    }
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

}
