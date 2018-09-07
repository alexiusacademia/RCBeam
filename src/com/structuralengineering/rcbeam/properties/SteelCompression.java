package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Conversions;

public class SteelCompression {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    /**
     * Total area of steel
     * Default is in square millimeters
     */
    private double totalArea;

    /**
     * Actual tensile stress in steel
     */
    private double fs;

    /**
     * Actual strain caused by stress.
     */
    private double strain;

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Gets the total area in steel in specified format.
     * If inMetric is true, in square millimeters.
     * If inMetric is false, in square inches.
     * @param inMetric Metric preference.
     * @return Steel area.
     */
    public double getTotalArea(boolean inMetric) {
        if (!inMetric) {
            // Convert to english (square inches)
            return Math.pow(Conversions.mmToIn(this.totalArea), 2);
        } else {
            return totalArea;
        }
    }

    /**
     * Returns the stess in the unit that is specified.
     * If inMetric is true, returns unit in MPa.
     * If inMetric is false, returns unit in PSI.
     * @param inMetric Metric unit preference.
     * @return fs (MPa/PSI)
     */
    public double getFs(boolean inMetric) {
        if (!inMetric) {
            return Conversions.MPatoPSI(fs);
        }

        return fs;
    }

    /**
     * Returns stress in MPa unit.
     * @return fs (MPa)
     */
    public double getFs() {
        return fs;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Sets the total area of steel in specified unit.
     * If inMetric is true, in square millimeter.
     * If inMetric is false, in square inches.
     * @param totalArea Area of steel.
     * @param inMetric Metric preference.
     */
    public void setTotalArea(double totalArea, boolean inMetric) {
        if (inMetric) {
            this.totalArea = totalArea;
        } else {
            this.totalArea = Math.pow(Conversions.inTomm(totalArea), 2);
        }
    }

    /**
     * Sets the stress (fs) in the unit specified.
     * if inMetric is true, fs should be in MPa.
     * if inMetric is false, fs should be in PSI.
     * @param fs steel stress
     * @param inMetric Metric unit preference.
     */
    public void setFs(double fs, boolean inMetric) {
        if (inMetric) {
            this.fs = fs;
        } else {
            this.fs = Conversions.PSItoMPa(fs);
        }
    }

    /**
     * Sets the stress in MPa unit (default).
     * @param fs steel stress.
     */
    public void setFs(double fs) {
        this.fs = fs;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Calculate the strain from the stress.
     */
    private void calculateStrainFromStress() {
        this.strain = this.fs / BeamContants.ES;
    }

    /**
     * Calculate strain from the strain diagram using similar triangles.
     * @param kd Distance from neutral axis to concrete extreme compression fiber.
     * @param d Effective depth of beam.
     * @param concreteStrain Strain in concrete extreme compression fiber.
     */
    public void calculateStrainFromDiagram(double dPrime, double kd, double d, double concreteStrain) {
        this.strain = concreteStrain / kd * (kd - dPrime);
    }
}
