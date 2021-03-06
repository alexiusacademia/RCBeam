package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Conversions;

import java.util.ArrayList;
import java.util.List;

public class BeamSection {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    private Section section;
    private SteelTension steelTension;                  // Tension steel property
    private SteelCompression steelCompression;          // Compression steel property
    private double effectiveDepth;                      // Depth of tension steel from concrete
    // extreme compression fiber in mm. (d)
    private double Ec;                                  // Concrete secant modulus in MPa. (Ec)
    private double Es;                                  // Modulus of elasticity of steel in MPa. (Es)
    private double modularRatio;                        // Modular ratio of steel to concrete. (n)
    private double concreteStrainIndex;                 // ⲉo
    private double β1;                                  // β1
    private double fcPrime = 0;                         // Concrete yield strength MPa
    private double fr;                                  // Modulus of rupture
    private double fy;                                  // Steel yield strength MPa
    private Unit unit;                                  // Unit to be used for all analysis

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Constructors
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    /**
     * Empty constructor
     * Sets the default unit to METRIC
     */
    public BeamSection() {
        // Initializations
        this.steelTension = new SteelTension();
        this.steelCompression = new SteelCompression();
        this.unit = Unit.METRIC;
    }

    /**
     * Constructor that sets the unit for analysis.
     *
     * @param u Unit
     */
    public BeamSection(Unit u) {
        this.unit = u;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Getters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Returns all the section nodes as ArrayList.
     *
     * @return section
     */
    public Section getSection() {
        return this.section;
    }

    /**
     * Gets the effective depth of beam.
     *
     * @return effectiveDepth (d)
     */
    public double getEffectiveDepth() {
        return Conversions.linearConverted(this.effectiveDepth, this.unit);
    }

    /**
     * Sets the effective depth of the beam.
     *
     * @param effectiveDepth effective depth
     */
    public void setEffectiveDepth(double effectiveDepth) {
        this.effectiveDepth = effectiveDepth;
        if (this.unit == Unit.ENGLISH) {
            this.effectiveDepth = Conversions.inTomm(effectiveDepth);
        }
    }

    /**
     * Gets the concrete secant modulus.
     *
     * @return Ec.
     */
    public double getEc() {
        return Conversions.pressureConverted(this.Ec, this.unit);
    }

    /**
     * Gets the modulus of elasticity of steel.
     *
     * @return Es in MPa.
     */
    public double getEs() {
        return Conversions.pressureConverted(this.Es, this.unit);
    }

    /**
     * Gets the modular ratio.
     *
     * @return modularRatio
     */
    public double getModularRatio() {
        return modularRatio;
    }

    /**
     * Sets the modular ratio.
     *
     * @param modularRatio n
     */
    public void setModularRatio(double modularRatio) {
        this.modularRatio = modularRatio;
    }

    /**
     * Gets the modulus of rupture.
     *
     * @return fr
     */
    public double getFr() {
        return Conversions.pressureConverted(this.fr, this.unit);
    }

    /**
     * Gets the steel tension object.
     *
     * @return steelTension
     */
    public SteelTension getSteelTension() {
        return steelTension;
    }

    /**
     * Sets SteelTension object.
     *
     * @param steelTension SteelTension object
     */
    public void setSteelTension(SteelTension steelTension) {
        this.steelTension = steelTension;
    }

    public SteelCompression getSteelCompression() {
        return steelCompression;
    }

    /**
     * Sets the SteelCompression object
     *
     * @param steelCompression SteelCompression object
     */
    public void setSteelCompression(SteelCompression steelCompression) {
        this.steelCompression = steelCompression;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * Gets the concrete compressive strength.
     *
     * @return fc'
     */
    public double getFcPrime() {
        return Conversions.pressureConverted(this.fcPrime, this.unit);
    }

    /**
     * Sets the fc'
     *
     * @param fcPrime Concrete compressive strength
     */
    public void setFcPrime(double fcPrime) {
        this.fcPrime = fcPrime;
        if (this.unit == Unit.ENGLISH) {
            this.fcPrime = Conversions.PSItoMPa(fcPrime);
        }
        this.fr = 0.6 * Math.sqrt(this.fcPrime);
        this.Ec = 4700 * Math.sqrt(this.fcPrime);
        this.modularRatio = BeamContants.ES / this.Ec;
        this.concreteStrainIndex = 2 * 0.85 * this.fcPrime / this.Ec;
    }

    /**
     * Get ⲉo
     *
     * @return ⲉo
     */
    public double getConcreteStrainIndex() {
        return concreteStrainIndex;
    }

    /**
     * Get the steel tebsile strength
     *
     * @return fy
     */
    public double getFy() {
        return Conversions.pressureConverted(this.fy, this.unit);
    }

    /**
     * Sets the fy.
     *
     * @param fy Steel yield strength.
     */
    public void setFy(double fy) {
        this.fy = fy;
        if (this.unit == Unit.ENGLISH) {
            this.fy = Conversions.PSItoMPa(fy);
        }
    }

    /**
     * Returns the unit
     *
     * @return unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the unit
     *
     * @param unit Unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Calculates the modular ratio.
     */
    private void calculateModularRatio() {
        this.modularRatio = this.Es / this.Ec;
    }

}
