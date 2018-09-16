package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.*;
import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the various reinforced concrete beam analysis
 */
public class BeamAnalyses {
    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Properties
    //
    // = = = = = = = = = = = = = = = = = = = = = =
    private BeamSection beamSection;                          // Beam section to be analyzed
    private double moment;                                    // Moment load in N-mm
    private SteelTension steelTension;                        // Steel in tension property.
    private SteelCompression steelCompression;                // Steel in compression property
    private double minimumSteelTensionArea;                   // Asmin, minimum reinforcement for the cracking stage
    private double crackingMoment;                            // Mcr in N-mm
    private double curvatureAfterCracking;

    /**
     * Constructor that provides the beam section to be analyzed
     *
     * @param bSection BeamSection
     */
    public BeamAnalyses(BeamSection bSection) {
        this.beamSection = bSection;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Getters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    public double getMinimumSteelTensionArea() {
        return minimumSteelTensionArea;
    }

    public double getCrackingMoment() {
        return crackingMoment;
    }

    public double getCurvatureAfterCracking() {
        return curvatureAfterCracking;
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    /**
     * Analyze the beam with the un-cracked section right before cracking.
     *
     * @return BeamAnalysisResult of the un-cracked section.
     */
    public BeamAnalysisResult beforeCrackAnalysis() {
        BeamAnalysisResult analysis = new BeamAnalysisResult();
        List<BeamSectionNode> beamSectionNodes = this.beamSection.getSection();

        double fcprime = beamSection.getFcPrime();
        double fr = beamSection.getFr();                                      // Modulus of rupture
        double Ec = beamSection.getEc();                                      // Concrete secant modulus
        double h = Calculators.highestY(beamSectionNodes) -
                Calculators.lowestY(beamSectionNodes);
        double Ac = Calculators.calculateArea(beamSectionNodes);              // Area of concrete alone
        double yc = Calculators.calculateCentroidY(beamSectionNodes);         // Calculate centroid from extreme compression fiber.
        double d = beamSection.getEffectiveDepth();
        double dPrime = beamSection.getSteelCompression().getdPrime(this.beamSection.getUnit());
        double fy = beamSection.getFy();

        steelTension = beamSection.getSteelTension();
        steelCompression = beamSection.getSteelCompression();
        double As = steelTension.getTotalArea(this.beamSection.getUnit());                  // Get the steel area in tension
        double AsPrime = steelCompression.getTotalArea(this.beamSection.getUnit());         // Get the steel area in compression
        double ⲉo = beamSection.getConcreteStrainIndex();                     // ⲉo

        double At = 0;                                                        // Total area of section (Transformed)
        double n = 1;                                                         // Modular ratio

        // Calculate total area including steel transformed
        n = beamSection.getModularRatio();
        At += Ac;
        At += (n - 1) * As;
        At += (n - 1) * AsPrime;

        // Calculate moments of areas
        double Ma = 0;
        Ma += (n - 1) * As * d;
        Ma += (n - 1) * AsPrime * dPrime;
        Ma += Ac * yc;

        double kd = 0;                                                        // Neutral axis to extreme compression fiber.
        kd = Ma / At;
        double kdY = Calculators.highestY(beamSectionNodes) - kd;             // Elevation of kd

        double ⲉc = (fr / Ec) / (h - kd) * kd;                                // Strain in concrete compression
        double fc = ⲉc * Ec;                                                  // Concrete stress
        double fs = (fr * BeamContants.ES * (d - kd)) / (Ec * (h - kd));
        double fsPrime = (fr * BeamContants.ES * (kd - dPrime)) / (Ec * (h - kd));
        double compressionArea = Calculators.getAreaAboveAxis(kdY, beamSectionNodes);
        double tensionArea = Calculators.calculateArea(beamSectionNodes) - compressionArea;
        double Cc, Cs, Tc, Ts;                                                // Resultant forces
        Cc = 1 / 2.0 * fc * compressionArea;                                  // Compression force on concrete
        Cs = AsPrime * fsPrime;                                               // Compression force on steel
        Tc = 1 / 2.0 * fr * tensionArea;                                      // Tensile force on concrete
        Ts = As * fs;                                                         // Tensile force at steel

        double ycc = (Cs * dPrime + Cc * kd / 3) / (Cs + Cc);                 // Location of compression resultant

        double Mcr = Ts * (d - ycc) + Tc * (h - ycc - (h - kd) / 3);
        double curvature = ⲉc / kd;
        this.crackingMoment = Mcr;

        analysis.setMomentC(Mcr);
        analysis.setCurvatureC(curvature);

        // Calculate minimum tensile reinforcement required by the code
        // Using the exact stress block distribution
        // Try for new kd
        kd = 0.001;
        double 𝜆o = 1;                                                        // Ductility factor
        double k2 = 0;                                                        // Compression resultant location factor
        double Lo = 0;
        double Mcalculated = 0;
        while (Mcalculated < Mcr) {
            kd += 0.001;
            ⲉc = fy * kd / (BeamContants.ES * (d - kd));
            𝜆o = ⲉc / ⲉo;
            k2 = 1 / 4.0 * (4 - 𝜆o) / (3 - 𝜆o);
            fc = ⲉc * Ec;
            Lo = solveForLo(𝜆o, true);
            kdY = Calculators.highestY(beamSectionNodes) - kd;
            compressionArea = Calculators.getAreaAboveAxis(kdY, beamSectionNodes);
            Mcalculated = Lo * fc * compressionArea * (d - k2 * kd);
        }
        ⲉc = fy * kd / (BeamContants.ES * (d - kd));
        𝜆o = ⲉc / ⲉo;
        k2 = 1 / 4.0 * (4 - 𝜆o) / (3 - 𝜆o);
        this.minimumSteelTensionArea = Mcr / (fy * (d - k2 * kd));
        this.curvatureAfterCracking = ⲉc / kd;
        return analysis;
    }

    public BeamAnalysisResult beamCapacityAnalysis(StressDistribution sd) {
        BeamAnalysisResult analysis = new BeamAnalysisResult();

        List<BeamSectionNode> nodes = this.beamSection.getSection();
        double ⲉcu = BeamContants.MAX_CONCRETE_STRAIN;
        double Es = BeamContants.ES;
        double d = this.beamSection.getEffectiveDepth();
        double dPrime = this.beamSection.getSteelCompression().getdPrime(Unit.METRIC);
        double fs,
                fy = this.beamSection.getFy(),
                As = this.beamSection.getSteelTension().getTotalArea(Unit.METRIC),
                AsPrime = this.beamSection.getSteelCompression().getTotalArea(Unit.METRIC),
                fsPrime,
                Cc = 0,
                Cs = 0,
                fcPrime = this.beamSection.getFcPrime(),
                fc = 0.85 * fcPrime,
                k2,
                Lo,
                kdY,
                compressionArea;

        double moment = 0;
        double AsCalc = 0;
        double kd = 0.001;

        if (sd == StressDistribution.PARABOLIC) {
            // Find kd
            double 𝜆o = 1;
            while (AsCalc < As) {
                Lo = solveForLo(𝜆o, true);
                kdY = Calculators.highestY(this.beamSection.getSection()) - kd;
                compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);

                Cc = Lo * fc * compressionArea;
                fs = ⲉcu * Es * (d - kd) / kd;

                if (fs >= fy) {
                    fs = fy;
                }
                fsPrime = fs * (kd - dPrime) / (d - kd);
                if (fsPrime > fy) {
                    fsPrime = fy;
                }

                Cs = AsPrime * fsPrime;
                AsCalc = (Cc + Cs) / fs;

                kd += 0.0001;

            }

            k2 = 1 / 4.0 * (4 - 𝜆o) / (3 - 𝜆o);
            moment = Cc * (d - k2 * kd) + Cs * (d - dPrime);
        } else {
            double beta = 0.85;

            if (fcPrime >= BeamContants.COMPRESSIVE_STRENGTH_THRESHOLD) {
                beta = 0.85 - 0.05 / 7 * (fcPrime - BeamContants.COMPRESSIVE_STRENGTH_THRESHOLD);
            }

            // Limit beta to 0.65
            if (beta < 0.65) {
                beta = 0.65;
            }

            double a = 1;           // Compression block height

            while (AsCalc < As) {
                kd = a / beta;
                fs = BeamContants.MAX_CONCRETE_STRAIN * BeamContants.ES * (d - kd) / kd;
                if (fs >= fy) {
                    fs = fy;
                }
                kdY = Calculators.highestY(this.beamSection.getSection()) - a;
                compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);

                Cc = fc * compressionArea;

                fsPrime = fs * (kd - dPrime) / (d - kd);
                if (fsPrime > fy) {
                    fsPrime = fy;
                }

                Cs = AsPrime * fsPrime;

                AsCalc = (Cc + Cs) / fs;
                moment = Cc * (d - a/2) + Cs * (d - dPrime);
                a += 0.0001;
            }

        }

        analysis.setMomentC(moment);

        return analysis;
    }

    /**
     * Analyze the beam right after the cracking occurs without additional loading.
     *
     * @return BeamAnalysisResult right after cracking.
     */
    public BeamAnalysisResult afterCrackAnalysis() {
        BeamAnalysisResult analysis = new BeamAnalysisResult();

        return analysis;
    }

    /**
     * Analyze the beam when the concrete reaches a specified amount of stress in
     * compression.
     *
     * @param fc The specified stress for the yielding of concrete.
     * @return BeamAnalysisResult for yield of concrete.
     */
    public BeamAnalysisResult concreteYieldAnalysis(double fc) {
        BeamAnalysisResult analysis = new BeamAnalysisResult();


        return analysis;
    }

    /**
     * Analyze the beam when the tension yields at a specified strength.
     *
     * @param fs Tension steel yield parameter
     * @return BeamAnalysisResult for yield of tension steel.
     */
    public BeamAnalysisResult steelYieldAnalysis(double fs) {
        BeamAnalysisResult analysis = new BeamAnalysisResult();

        return analysis;
    }

    /**
     * Analyze the beam for the inelastic stage.
     * 1st stage is 0 < ec < eo
     * 2nd stage is eo < ec < ecu
     *
     * @return List of BeamAnalysisResult for the inelastic stage.
     */
    public List<BeamAnalysisResult> inelasticStageAnalysis() {
        List<BeamAnalysisResult> analyses = new ArrayList<>();

        return analyses;
    }

    private double solveForLo(double ductilityFactor, boolean isElastic) {
        if (isElastic) {
            return 0.85 / 3 * ductilityFactor * (3 - ductilityFactor);
        } else {
            return 0.85 * (3 * ductilityFactor - 1) / (3 * ductilityFactor);
        }
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }

}
