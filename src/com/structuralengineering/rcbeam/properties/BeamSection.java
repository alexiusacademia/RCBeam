package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.BeamContants;

import java.util.ArrayList;
import java.util.List;

public class BeamSection {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    private List<BeamSectionNode> section;              // Beam section definition.
    private SteelTension steelTension;                  // Tension steel property
    private double effectiveDepth;                      // Depth of tension steel from concrete
                                                        // extreme compression fiber in mm. (d)
    private double dPrime;                              // Distance of compression steel to concrete
                                                        // extreme compression fiber in mm. (d')
    private double Ec;                                  // Concrete secant modulus in MPa. (Ec)
    private double Es;                                  // Modulus of elasticity of steel in MPa. (Es)
    private double modularRatio;                        // Modular ratio of steel to concrete. (n)
    private double concreteStrainIndex;                 // ⲉo
    private double β1;                                  // β1
    private double fcPrime = 0;                         // Concrete yield strength MPa
    private double fr;                                  // Modulus of rupture
    private double fy = 0;                              // Steel yield strength MPa

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Constructors
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Constructor with defined section nodes
     *
     * @param nodes A list of x,y coordinate that defines the beam geometry
     */
    public BeamSection(List<BeamSectionNode> nodes) {
        // Initializations
        this.steelTension = new SteelTension();

        // Assignment
        this.section = nodes;
    }

    /**
     * Empty constructor
     */
    public BeamSection() {
        // Initializations
        this.section = new ArrayList<>();
        this.steelTension = new SteelTension();
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
    public List<BeamSectionNode> getSection() {
        return this.section;
    }

    /**
     * Gets the effective depth of beam.
     * @return effectiveDepth (d)
     */
    public double getEffectiveDepth() {
        return effectiveDepth;
    }

    /**
     * Gets the d'
     * @return d'
     */
    public double getdPrime() {
        return dPrime;
    }

    /**
     * Gets the concrete secant modulus.
     * @return Ec in MPa.
     */
    public double getEc() {
        return Ec;
    }

    /**
     * Gets the modulus of elasticity of steel.
     * @return Es in MPa.
     */
    public double getEs() {
        return Es;
    }

    /**
     * Gets the modular ratio.
     * @return modularRatio
     */
    public double getModularRatio() {
        return modularRatio;
    }

    /**
     * Gets the modulus of rupture.
     * @return fr
     */
    public double getFr() {
        return fr;
    }

    /**
     * Gets the steel tension object.
     * @return steelTension
     */
    public SteelTension getSteelTension() {
        return steelTension;
    }

    /**
     * Gets the concrete compressive strength.
     * @return fc'
     */
    public double getFcPrime() {
        return fcPrime;
    }

    /**
     * Get ⲉo
     * @return ⲉo
     */
    public double getConcreteStrainIndex() {
        return concreteStrainIndex;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Sets the fy.
     * @param fy Steel yield strength.
     */
    public void setFy(double fy) {
        this.fy = fy;
    }

    /**
     * Sets the fc'
     * @param fcPrime Concrete compressive strength
     */
    public void setFcPrime(double fcPrime) {
        this.fcPrime = fcPrime;
        this.fr = 0.7 * Math.sqrt(fcPrime);
        this.Ec = 4700 * Math.sqrt(fcPrime);
        this.modularRatio = BeamContants.ES / this.Ec;
        this.concreteStrainIndex = 2 * 0.85 * this.fcPrime / this.Ec;
    }

    /**
     * Sets the effective depth of the beam.
     * @param effectiveDepth
     */
    public void setEffectiveDepth(double effectiveDepth) {
        this.effectiveDepth = effectiveDepth;
    }

    /**
     * Sets the d'
     * @param dPrime
     */
    public void setdPrime(double dPrime) {
        this.dPrime = dPrime;
    }

    /**
     * Sets the modular ratio.
     * @param modularRatio n
     */
    public void setModularRatio(double modularRatio) {
        this.modularRatio = modularRatio;
    }

    /**
     * Sets SteelTension object.
     * @param steelTension SteelTension object
     */
    public void setSteelTension(SteelTension steelTension) {
        this.steelTension = steelTension;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Add a single node to the beam section. Addition of nodes
     * must follow a clockwise notation.
     * @param node BeamSectionNode to be added.
     */
    public void addNode(BeamSectionNode node) {
        this.section.add(node);
    }

    /**
     * Remove a node at a specified index.
     * @param index The index in which the item is to be removed from.
     */
    public void removeNode(int index) {
        // Check if index exist
        if ((index - 1) < this.section.size()) {
            // Index surely exist
            // Remove the item
            this.section.remove(index);
        }
    }

    /**
     * Calculates the modular ratio.
     */
    private void calculateModularRatio() {
        this.modularRatio = this.Es / this.Ec;
    }
}
