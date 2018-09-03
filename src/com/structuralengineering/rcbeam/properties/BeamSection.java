package com.structuralengineering.rcbeam.properties;

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
    private double ductilityFactor;                     // Ductility factor, 𝜆
    private double Lo;                                  // k1 * k2
    private double ⲉo;                                  // ⲉo
    private double β1;                                  // β1
    private double fcPrime = 0;                         // Concrete yield strength MPa
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
        this.section = new ArrayList<>();
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
        return section;
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
     * Gets the steel tension object.
     * @return steelTension
     */
    public SteelTension getSteelTension() {
        return steelTension;
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
     * Sets the concrete secant modulus.
     * @param ec Ec
     */
    public void setEc(double ec) {
        this.Ec = ec;
    }

    /**
     * Sets the modulus of elasticity of steel.
     * @param es in MPa.
     */
    public void setEs(double es) {
        Es = es;
    }

    /**
     * Sets the modular ratio.
     * @param modularRatio n
     */
    public void setModularRatio(double modularRatio) {
        this.modularRatio = modularRatio;
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
     * Sets the steel in tension area.
     * @param area Area of steel.
     * @param inMetric Metric preference.
     */
    public void setSteelTension(double area, boolean inMetric) {
        this.steelTension.setTotalArea(area, inMetric);
    }

    /**
     * Calculates the modular ratio.
     */
    private void calculateModularRatio() {
        this.modularRatio = this.Es / this.Ec;
    }
}
