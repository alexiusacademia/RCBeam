package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.BeamAnalysisResult;
import com.structuralengineering.rcbeam.analysis.StressDistribution;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;
import com.structuralengineering.rcbeam.properties.Unit;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        // Rectangular
        bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(0,450));
        bs.addNode(new BeamSectionNode(300,450));
        bs.addNode(new BeamSectionNode(300,0));
        // Triangular
        /*bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(187.5,450));
        bs.addNode(new BeamSectionNode(375, 0));*/
        // T-Beam
        /*bs.addNode(new BeamSectionNode(0, 0));
        bs.addNode(new BeamSectionNode(0, 360));
        bs.addNode(new BeamSectionNode(-1150, 360));
        bs.addNode(new BeamSectionNode(-1150, 485));
        bs.addNode(new BeamSectionNode(1400, 485));
        bs.addNode(new BeamSectionNode(1400, 360));
        bs.addNode(new BeamSectionNode(250, 360));
        bs.addNode(new BeamSectionNode(250, 0));*/
        // Rectangular
        /*bs.addNode(new BeamSectionNode(0, 0));
        bs.addNode(new BeamSectionNode(0, 500));
        bs.addNode(new BeamSectionNode(300, 500));
        bs.addNode(new BeamSectionNode(300, 0));*/

        bs.setFcPrime(21);
        bs.setEffectiveDepth(400);
        bs.setFy(275);

        SteelTension st = new SteelTension();
      
        st.setTotalArea(17000, Unit.METRIC);

        bs.setSteelTension(st);

        BeamAnalyses analyses = new BeamAnalyses(bs);

        BeamAnalysisResult limitAnalysis1 = analyses.beamCapacityAnalysis(StressDistribution.WHITNEY);
        BeamAnalysisResult balancedAnalysis1 = analyses.balancedAnalysis(StressDistribution.WHITNEY);
        double Mn = limitAnalysis1.getMomentC();

        printString("Mn (Whitney) = " + String.valueOf(Math.round(Mn * 1000) / 1000 / Math.pow(1000, 2)));
        printString("kd (Whitney) = " + limitAnalysis1.getKd());
        printString("Asb (Whitney) = " + analyses.getBalacedSteelTension());

        BeamAnalysisResult limitAnalysis2 = analyses.beamCapacityAnalysis(StressDistribution.PARABOLIC);
        BeamAnalysisResult balancedAnalysis2 = analyses.balancedAnalysis(StressDistribution.PARABOLIC);
        Mn = limitAnalysis2.getMomentC();

        printString("Mn (Parabolic) = " + String.valueOf(Math.round(Mn * 1000) / 1000 / Math.pow(1000, 2)));
        printString("kd (Parabolic) = " + limitAnalysis2.getKd());
        printString("Asb (Parabolic) = " + analyses.getBalacedSteelTension());
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
