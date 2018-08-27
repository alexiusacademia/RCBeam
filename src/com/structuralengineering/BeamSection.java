package com.structuralengineering;

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
  public void addNode(BeamSectionNode node) {
    this.section.add(node);
  }
}
