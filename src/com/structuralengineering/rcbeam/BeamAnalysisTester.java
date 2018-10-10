package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.BeamAnalysisResult;
import com.structuralengineering.rcbeam.analysis.StressDistribution;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.Node;
import com.structuralengineering.rcbeam.properties.SteelTension;
import com.structuralengineering.rcbeam.properties.Unit;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        // Rectangular
        /*bs.addNode(new Node(0,0));
        bs.addNode(new Node(0,450));
        bs.addNode(new Node(300,450));
        bs.addNode(new Node(300,0));*/
        // Triangular
        bs.addNode(new Node(0, 0));
        bs.addNode(new Node(150, 600));
        bs.addNode(new Node(300, 0));
        // T-Beam
        /*bs.addNode(new Node(0, 0));
        bs.addNode(new Node(0, 360));
        bs.addNode(new Node(-1150, 360));
        bs.addNode(new Node(-1150, 485));
        bs.addNode(new Node(1400, 485));
        bs.addNode(new Node(1400, 360));
        bs.addNode(new Node(250, 360));
        bs.addNode(new Node(250, 0));*/
        // Rectangular
        /*bs.addNode(new Node(0, 0));
        bs.addNode(new Node(0, 500));
        bs.addNode(new Node(300, 500));
        bs.addNode(new Node(300, 0));*/

        bs.setFcPrime(20);
        bs.setEffectiveDepth(550);
        bs.setFy(300);

        SteelTension st = new SteelTension();

        st.setTotalArea(1000, Unit.METRIC);

        bs.setSteelTension(st);

        BeamAnalyses analyses = new BeamAnalyses(bs);
        BeamAnalysisResult nominalParabolic = analyses.beamCapacityAnalysis(StressDistribution.PARABOLIC);
        printString("Mn(parabolic) = " + nominalParabolic.getMomentC() / Math.pow(1000, 2));

        BeamAnalysisResult nominalWhitney = analyses.beamCapacityAnalysis(StressDistribution.WHITNEY);
        printString("Mn(Whitney) = " + nominalWhitney.getMomentC() / Math.pow(1000, 2));

        BeamAnalysisResult uncrack = analyses.uncrackedAnalysis();
        printString("Mcr = " + analyses.getCrackingMoment());
        printString("Asmin = " + analyses.getMinimumSteelTensionArea());
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
