package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.Node;
import com.structuralengineering.rcbeam.properties.Section;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

public class BeamSectionTester {
    public static void main(String[] args) {
        BeamSection bs = new BeamSection();

        List<Node> mainSection = new ArrayList<>();

        mainSection.add(new Node(0,0));
        mainSection.add(new Node(0,10));
        mainSection.add(new Node(10,10));
        mainSection.add(new Node(10,0));
        mainSection.add(new Node(0,0));

        List<Node> clip1 = new ArrayList<>();
        clip1.add(new Node(2, 2));
        clip1.add(new Node(2, 8));
        clip1.add(new Node(8, 8));
        clip1.add(new Node(8, 2));

        Section section = new Section();
        if (section.setMainSection(mainSection)) {
            if (section.addClipping(clip1)) {
                printString("Width at (el. 5.0) : " + String.valueOf(section.getEffectiveWidth(5)));
                printString("Area : " + section.grossAreaOfConcrete());
            }
        }
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
