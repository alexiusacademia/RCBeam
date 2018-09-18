package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.Conversions;

public class SteelTension {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    private double totalArea;                           // Total area of steel. Default is in square millimeters
    private double fs;                                  // Actual tensile stress in steel
    private double strain;                              // Actual strain caused by stress.

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Getters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Gets the total area in steel in specified format.
     * If inMetric is true, in square millimeters.
     * If inMetric is false, in square inches.
     *
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
     *
     * @param u Unit.
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
     * Sets the total area of steel in specified unit.
     *
     * @param totalArea Area of steel.
     * @param u         Unit
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
     *
     * @param fs steel stress
     * @param u  Unit
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
