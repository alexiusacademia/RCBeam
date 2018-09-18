package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;

public class BeamSectionTester {
    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        bs.addNode(new BeamSectionNode(0, 0));
        bs.addNode(new BeamSectionNode(0, 400));
        bs.addNode(new BeamSectionNode(-150, 400));
        bs.addNode(new BeamSectionNode(-150, 520));
        bs.addNode(new BeamSectionNode(350, 520));
        bs.addNode(new BeamSectionNode(350, 400));
        bs.addNode(new BeamSectionNode(200, 400));
        bs.addNode(new BeamSectionNode(200, 0));


    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
