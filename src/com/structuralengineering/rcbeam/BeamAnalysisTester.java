package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.BeamAnalysisResult;
import com.structuralengineering.rcbeam.analysis.StressDistribution;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;
import com.structuralengineering.rcbeam.properties.Unit;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.List;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        // Rectangular
        /* bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(0,450));
        bs.addNode(new BeamSectionNode(300,450));
        bs.addNode(new BeamSectionNode(300,0)); */
        // Triangular
        /* bs.addNode(new BeamSectionNode(0,0));
        bs.addNode(new BeamSectionNode(150,600));
        bs.addNode(new BeamSectionNode(300, 0)); */
        // T-Beam
        bs.addNode(new BeamSectionNode(0, 0));
        bs.addNode(new BeamSectionNode(0, 360));
        bs.addNode(new BeamSectionNode(-1150, 360));
        bs.addNode(new BeamSectionNode(-1150, 485));
        bs.addNode(new BeamSectionNode(1400, 485));
        bs.addNode(new BeamSectionNode(1400, 360));
        bs.addNode(new BeamSectionNode(250, 360));
        bs.addNode(new BeamSectionNode(250, 0));

        bs.setFcPrime(20);
        bs.setEffectiveDepth(435);
        bs.setFy(300);

        SteelTension st = new SteelTension();
      
        st.setTotalArea(14500, Unit.METRIC);

        bs.setSteelTension(st);

        BeamAnalyses analyses = new BeamAnalyses(bs);
        BeamAnalysisResult limitAnalysis1 = analyses.beamCapacityAnalysis(StressDistribution.WHITNEY);
        double Mn = limitAnalysis1.getMomentC()/ Math.pow(1000, 2);
        printString("Mn (Whitney) = " + String.valueOf(Math.round(Mn)) + " kN-m");
        BeamAnalysisResult limitAnalysis2 = analyses.beamCapacityAnalysis(StressDistribution.PARABOLIC);
        Mn = limitAnalysis2.getMomentC()/ Math.pow(1000, 2);
        printString("Mn (Parabolic) = " + String.valueOf(Math.round(Mn)) + " kN-m");
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
