package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;

public class BeamSectionTester {
  public static void main(String[] args) {
    BeamSection bs = new BeamSection();
    bs.addNode(new BeamSectionNode(0,0));
    bs.addNode(new BeamSectionNode(0,500));
    bs.addNode(new BeamSectionNode(300,500));
    bs.addNode(new BeamSectionNode(300,0));

    for (BeamSectionNode n : bs.getSection()) {
      System.out.println("x = " + n.getX() + ", y = " + n.getY());
    }
  }
}
