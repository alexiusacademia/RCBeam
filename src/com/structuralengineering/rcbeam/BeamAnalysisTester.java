package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(0,450));
        bs.addNode(new BeamSectionNode(300,450));
        bs.addNode(new BeamSectionNode(300,0));

        SteelTension st = new SteelTension();
        st.setTotalArea(1500, true);


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
