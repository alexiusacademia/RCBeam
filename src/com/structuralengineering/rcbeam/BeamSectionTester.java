package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.utils.Calculators;

public class BeamSectionTester {
  public static void main(String[] args) {
    BeamSection bs = new BeamSection();
    bs.addNode(new BeamSectionNode(0,0));
    bs.addNode(new BeamSectionNode(0,400));
    bs.addNode(new BeamSectionNode(-150,400));
    bs.addNode(new BeamSectionNode(-150,520));
    bs.addNode(new BeamSectionNode(350, 520));
    bs.addNode(new BeamSectionNode(350,400));
    bs.addNode(new BeamSectionNode(200,400));
    bs.addNode(new BeamSectionNode(200,0));

    double area = Calculators.calculateArea(bs.getSection());
    double y = Calculators.calculateCentroidY(bs.getSection());
    printString("Area = " + String.valueOf(area));
    printString("Centroid = " + String.valueOf(y));
  }

  private static void printString(String str) {
    System.out.println(str);
  }

  private static void printLine() {
    printString("= = = = = = = = = = = = = = = = = = =");
  }
}
