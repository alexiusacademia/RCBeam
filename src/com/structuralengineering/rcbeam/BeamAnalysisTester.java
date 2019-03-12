package com.structuralengineering.rcbeam;

import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.BeamAnalysisResult;
import com.structuralengineering.rcbeam.analysis.StressDistribution;
import com.structuralengineering.rcbeam.properties.*;

import java.util.ArrayList;
import java.util.List;

public class BeamAnalysisTester {

    public static void main(String[] args) {
        BeamSection bs = new BeamSection();
        bs.setUnit(Unit.METRIC);

        List<Node> nodes = new ArrayList<>();
        // Rectangular
        /*bs.addNode(new Node(0,0));
        bs.addNode(new Node(0,450));
        bs.addNode(new Node(300,450));
        bs.addNode(new Node(300,0));*/
        // Triangular
        /*nodes.add(new Node(0, 0));
        nodes.add(new Node(150, 600));
        nodes.add(new Node(300, 0));*/

        // T-Beam
        nodes.add(new Node(0, 0));
        nodes.add(new Node(0, 300));
        nodes.add(new Node(-300, 300));
        nodes.add(new Node(-300, 400));
        nodes.add(new Node(500, 400));
        nodes.add(new Node(500, 300));
        nodes.add(new Node(200, 300));
        nodes.add(new Node(200, 0));
        // Rectangular
        /*bs.addNode(new Node(0, 0));
        bs.addNode(new Node(0, 500));
        bs.addNode(new Node(300, 500));
        bs.addNode(new Node(300, 0));*/

        Section section = new Section();
        section.setMainSection(nodes);
        bs.setSection(section);

        bs.setFcPrime(20);
        bs.setEffectiveDepth(340);
        bs.setFy(400);

        SteelTension st = new SteelTension();
        SteelCompression sc = new SteelCompression();

        st.setTotalArea(3000, Unit.METRIC);
        sc.setdPrime(60, Unit.METRIC);
        sc.setTotalArea(500, Unit.METRIC);

        bs.setSteelTension(st);
        bs.setSteelCompression(sc);

        printString("n = " + bs.getModularRatio());
        printString("fr = " + bs.getFr());

        BeamAnalyses analyses = new BeamAnalyses(bs);


        BeamAnalysisResult nominalParabolic = analyses.beamCapacityAnalysis(StressDistribution.PARABOLIC);
        printString("Mn(parabolic) = " + nominalParabolic.getMomentC() / Math.pow(1000, 2));

        BeamAnalysisResult nominalWhitney = analyses.beamCapacityAnalysis(StressDistribution.WHITNEY);
        printString("Mn(Whitney) = " + nominalWhitney.getMomentC() / Math.pow(1000, 2));
        /*
        BeamAnalysisResult uncrack = analyses.uncrackedAnalysis();
        printString("Mcr = " + analyses.getCrackingMoment());
        printString("Asmin = " + analyses.getMinimumSteelTensionArea());
        printString("Mcr = " + uncrack.getMomentC());
        */
        BeamAnalysisResult bal = analyses.balancedAnalysis(StressDistribution.WHITNEY);
        printString("Amax = " + .75*analyses.getBalacedSteelTension());
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }
}
