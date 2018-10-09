package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.Node;
import com.structuralengineering.rcbeam.utils.Calculators;

public class BeamSectionTester {
    public static void main(String[] args) {
        BeamSection bs = new BeamSection();

        bs.addNode(new Node(0,0));
        bs.addNode(new Node(0,10));
        bs.addNode(new Node(10,10));
        bs.addNode(new Node(10,0));
        bs.addNode(new Node(0,0));
        bs.addNode(new Node(2,2));
        bs.addNode(new Node(8,2));
        bs.addNode(new Node(8,8));
        bs.addNode(new Node(2,8));
        bs.addNode(new Node(2,2));

        Double str = Calculators.getBaseAtY(5, bs.getSection());

        printString(String.valueOf(str));
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
