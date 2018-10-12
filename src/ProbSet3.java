import com.structuralengineering.rcbeam.analysis.BeamAnalyses;
import com.structuralengineering.rcbeam.analysis.StressDistribution;
import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;

public class ProbSet3 {
    public static void main(String[] args) {

        BeamSection bs = new BeamSection();
        bs.addNode(new BeamSectionNode(0, 0));
        bs.addNode(new BeamSectionNode(0, 620));
        bs.addNode(new BeamSectionNode(375, 620));
        bs.addNode(new BeamSectionNode(375, 0));

        bs.setEffectiveDepth(570);
        bs.setFy(275);
        bs.setFcPrime(27.5);

        BeamAnalyses analyses = new BeamAnalyses(bs);
        analyses.balancedAnalysis(StressDistribution.WHITNEY);

        printString(String.valueOf(analyses.getBalacedSteelTension()));

    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = =");
    }
}
