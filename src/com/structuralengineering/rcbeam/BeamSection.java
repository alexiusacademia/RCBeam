package com.structuralengineering.rcbeam;

import java.util.List;

public class BeamSection {
  private List<BeamSectionNode> section;

  /**
   * Constructor with defined section nodes
   * @param nodes A list of x,y coordinate that defines the beam geometry
   */
  public BeamSection(List<BeamSectionNode> nodes) {
    this.section = nodes;
  }

  /**
   * Empty constructor
   */
  public BeamSection() { }

  /**
   * ******************************************
   * Methods
   * ******************************************
   */

  /**
   * Adds a single node to the beam section. Addition of nodes
   * must follow a clockwise notation.
   * @param node BeamSectionNode to be added.
   */
  public void addNode(BeamSectionNode node) {
    this.section.add(node);
  }

  /**
   * Removes a node at a specified index
   * @param index
   */
  public void removeNode(int index) {

  }
}
