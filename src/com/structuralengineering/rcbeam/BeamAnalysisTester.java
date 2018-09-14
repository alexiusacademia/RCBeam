package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.BeamAnalysisResult;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.List;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        // Rectangular
        bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(0,500));
        bs.addNode(new BeamSectionNode(300,500));
        bs.addNode(new BeamSectionNode(300,0));
        // Triangular
        /* bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(150,600));
        bs.addNode(new BeamSectionNode(300, 0)); */

        bs.setFcPrime(21);
        bs.setEffectiveDepth(450);
        bs.setFy(275);

        SteelTension st = new SteelTension();
        st.setTotalArea(1200, true);

        bs.setSteelTension(st);

        BeamAnalyses analyses = new BeamAnalyses(bs);
        BeamAnalysisResult result = analyses.beforeCrackAnalysis();

        printString("Mcr = " + String.valueOf(result.getMomentC() / Math.pow(1000, 2)));
        printString("Curvature = " + String.valueOf(result.getCurvatureC()));
        printString("As(min) = " + String.valueOf(analyses.getMinimumSteelTensionArea()));
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
