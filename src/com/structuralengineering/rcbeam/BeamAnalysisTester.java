package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.List;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(0,450));
        bs.addNode(new BeamSectionNode(300,450));
        bs.addNode(new BeamSectionNode(300,0));

        printString("Beam nodes = " + bs.getSection().size());

        bs.setFcPrime(21);
        bs.setEffectiveDepth(400);

        SteelTension st = new SteelTension();
        st.setTotalArea(4539.927, true);

        bs.setSteelTension(st);

        BeamAnalyses analyses = new BeamAnalyses(bs);
        analyses.beforeCrackAnalysis();
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
