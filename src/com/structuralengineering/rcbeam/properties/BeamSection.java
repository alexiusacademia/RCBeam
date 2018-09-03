package com.structuralengineering.rcbeam.properties;

import java.util.ArrayList;
import java.util.List;

public class BeamSection {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    /**
     * Beam section definition.
     */
    private List<BeamSectionNode> section;

    private SteelTension steelTension;

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

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Setters
    //
    // = = = = = = = = = = = = = = = = = = = = = =


    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Add a single node to the beam section. Addition of nodes
     * must follow a clockwise notation.
     *
     * @param node BeamSectionNode to be added.
     */
    public void addNode(BeamSectionNode node) {
        this.section.add(node);
    }

    /**
     * Remove a node at a specified index
     *
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
}
