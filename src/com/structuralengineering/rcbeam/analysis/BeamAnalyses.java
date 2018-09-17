package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.*;
import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Calculators;
import com.structuralengineering.rcbeam.utils.Conversions;

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
    private BeamSection beamSection;                            // Beam section to be analyzed
    private double moment;                                      // Moment load in N-mm
    private SteelTension steelTension;                          // Steel in tension property.
    private SteelCompression steelCompression;                  // Steel in compression property
    private double minimumSteelTensionArea;                     // Asmin, minimum reinforcement for the cracking stage
    private double crackingMoment;                              // Mcr in N-mm
    private double curvatureAfterCracking;
    private double balacedSteelTension;                         // Required steel area for balanced design
    private Unit unit;

    /**
     * Constructor that provides the beam section to be analyzed
     *
     * @param bSection BeamSection
     */
    public BeamAnalyses(BeamSection bSection) {
        this.beamSection = bSection;
        this.unit = bSection.getUnit();
    }

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Getters
    //
    // = = = = = = = = = = = = = = = = = = = = = =

    public double getMinimumSteelTensionArea() {
        if (this.unit == Unit.ENGLISH) {
            return Conversions.toSquareInches(minimumSteelTensionArea);
        } else {
            return minimumSteelTensionArea;
        }
    }

    public double getCrackingMoment() {
        if (this.unit == Unit.ENGLISH) {
            return Conversions.toEnglishMoment(crackingMoment);
        } else {
            return crackingMoment;
        }
    }

    public double getCurvatureAfterCracking() {
        return curvatureAfterCracking;
    }

    public double getBalacedSteelTension() {
        if (this.unit == Unit.ENGLISH) {
            return Conversions.toSquareInches(this.balacedSteelTension);
        } else {
            return balacedSteelTension;
        }
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
        double Asmin = 0;
        double Tsmin = 0;
        Tsmin = Cc + Cs - Tc;
        Asmin = Tsmin / fy;

        this.minimumSteelTensionArea = Asmin;
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
        double fs = 0,
                fy = this.beamSection.getFy(),
                As = this.beamSection.getSteelTension().getTotalArea(Unit.METRIC),
                AsPrime = this.beamSection.getSteelCompression().getTotalArea(Unit.METRIC),
                fsPrime,
                Cc = 0,
                Cs = 0,
                fcPrime = this.beamSection.getFcPrime(),
                fc = 0.85 * fcPrime,
                kdY = 0,
                compressionArea;

        double moment = 0;
        double AsCalc = 0;
        double kd = 0.1;
        double highestElev = Calculators.highestY(nodes);

        if (sd == StressDistribution.PARABOLIC) {
            // Find kd
            int iterator = 500;                                            // Divide kd by this number
            double y, b;                                                    // Distance from neutral axis and corresponding beam width
            double dy;                                                      // Strip for integration
            double ⲉcy;                                                     // Concrete strain at y
            double yElev;
            while (AsCalc < As) {
                Cc = 0;                                                     // Reset Cc
                Cs = 0;
                dy = kd / iterator;
                for (int i = iterator; i > 0; i--) {
                    y = i * dy;
                    ⲉcy = ⲉcu * y / kd;
                    fc = 0.85 * fcPrime * (2 * ⲉcy / ⲉcu - Math.pow((ⲉcy / ⲉcu), 2));
                    yElev = highestElev - kd + y;
                    b = Calculators.getBaseAtY(yElev, nodes);
                    Cc += fc * b * dy;

                    fs = ⲉcu * Es * (d - kd) / kd;

                    if (fs >= fy) {
                        fs = fy;
                    }

                    fsPrime = fs * (kd - dPrime) / (d - kd);
                    if (fsPrime > fy) {
                        fsPrime = fy;
                    }

                    Cs += AsPrime * fsPrime;
                }
                AsCalc = (Cc + Cs) / fs;
                kd += 0.001;
            }

            double My = 0;
            double compressionSolid = Cc;
            double compressionSteel = Cs;
            double yBar;                                                    // Centroid of compression solid from top
            dy = kd / iterator;                                             // Reset dy
            for (int i = iterator; i > 0; i--) {
                y = i * dy;
                ⲉcy = ⲉcu * y / kd;
                fc = 0.85 * fcPrime * (2 * ⲉcy / ⲉcu - Math.pow((ⲉcy / ⲉcu), 2));
                yElev = highestElev - kd + y;
                b = Calculators.getBaseAtY(yElev, nodes);
                Cc = fc * b * dy;

                My += Cc * (kd - y);
            }

            yBar = My / compressionSolid;

            Cc = compressionSolid;
            Cs = compressionSteel;
            moment = Cc * (d - yBar) + Cs * (d - dPrime);

        } else {
            double beta = calculateBeta(fcPrime);

            double a = 1;           // Compression block height

            while (AsCalc < As) {
                kd = a / beta;
                fs = ⲉcu * Es * (d - kd) / kd;
                if (fs >= fy) {
                    fs = fy;
                }
                kdY = Calculators.highestY(nodes) - a;
                compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);

                Cc = fc * compressionArea;

                fsPrime = fs * (kd - dPrime) / (d - kd);
                if (fsPrime > fy) {
                    fsPrime = fy;
                }

                Cs = AsPrime * fsPrime;

                AsCalc = (Cc + Cs) / fs;
                a += 0.001;
            }

            double compressionCentroid;
            compressionCentroid = Calculators.getCentroidAboveAxis(kdY, nodes);

            moment = Cc * (d - compressionCentroid) + Cs * (d - dPrime);
        }

        analysis.setMomentC(moment);
        analysis.setKd(kd);
        analysis.setCurvatureC(ⲉcu / kd);

        return analysis;
    }

    /**
     * Analysis for balanced steel design
     * @param sd Stress distribution block
     * @return analysis
     */
    public BeamAnalysisResult balancedAnalysis(StressDistribution sd) {
        BeamAnalysisResult result = new BeamAnalysisResult();

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
                kdY = 0,
                compressionArea;
        double kd;
        double Asb = 0;

        kd = ⲉcu * Es * d / (fy + ⲉcu * Es);

        if (sd == StressDistribution.PARABOLIC) {

        } else {
            // Whitney stress block
            double beta = calculateBeta(fcPrime);
            double a;
            a = beta * kd;
            kdY = Calculators.highestY(nodes) - a;
            compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);
            Cc = 0.85 * fcPrime * compressionArea;

            Asb = Cc / fy;
        }
        this.balacedSteelTension = Asb;
        result.setKd(kd);
        return result;
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

    private double calculateBeta(double fcPrime) {
        double beta = 0.85;

        if (fcPrime >= BeamContants.COMPRESSIVE_STRENGTH_THRESHOLD) {
            beta = 0.85 - 0.05 / 7 * (fcPrime - BeamContants.COMPRESSIVE_STRENGTH_THRESHOLD);
        }

        // Limit beta to 0.65
        if (beta < 0.65) {
            beta = 0.65;
        }

        return beta;
    }
}
