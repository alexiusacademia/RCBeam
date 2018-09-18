package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.*;
import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Calculators;
import com.structuralengineering.rcbeam.utils.Conversions;

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

        double fr = beamSection.getFr();                                      // Modulus of rupture
        double Ec = beamSection.getEc();                                      // Concrete secant modulus
        double h = Calculators.highestY(beamSectionNodes) -
                Calculators.lowestY(beamSectionNodes);
        double Ac = Calculators.calculateArea(beamSectionNodes);              // Area of concrete alone
        double yc = Calculators.calculateCentroidY(beamSectionNodes);         // Calculate centroid from extreme compression fiber.
        double d = beamSection.getEffectiveDepth();
        double dPrime = beamSection.getSteelCompression().getdPrime(this.beamSection.getUnit());
        double fy = beamSection.getFy();

        SteelTension steelTension = beamSection.getSteelTension();
        SteelCompression steelCompression = beamSection.getSteelCompression();
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
        double highestElev = Calculators.highestY(beamSectionNodes);
        double ⲉc = (fr / Ec) / (h - kd) * kd;                                // Strain in concrete compression
        double fc = ⲉc * Ec;                                                  // Concrete stress
        double fs = (fr * BeamContants.ES * (d - kd)) / (Ec * (h - kd));
        double fsPrime = (fr * BeamContants.ES * (kd - dPrime)) / (Ec * (h - kd));
        double compressionArea = Calculators.getAreaAboveAxis(kdY, beamSectionNodes);
        double tensionArea = Calculators.calculateArea(beamSectionNodes) - compressionArea;
        double Cc, Cs, Tc, Ts;                                                // Resultant forces

        Cc = compressionSolidVolumeTriangular(kd, highestElev, h, fr);
        printString("Cc = " + String.valueOf(Cc));
        Cs = AsPrime * fsPrime;                                               // Compression force on steel
        // Tc = 1 / 2.0 * fr * tensionArea;                                      // Tensile force on concrete
        Ts = As * fs;                                                         // Tensile force at steel
        Tc = 0;
        double ycc = (Cs * dPrime + Cc * kd / 3) / (Cs + Cc);                 // Location of compression resultant

        double Mcr = Ts * (d - ycc) + Tc * (h - ycc - (h - kd) / 3);
        double curvature = ⲉc / kd;
        this.crackingMoment = Mcr;

        analysis.setMomentC(Mcr);
        analysis.setCurvatureC(curvature);

        // Calculate minimum tensile reinforcement required by the code
        double Asmin = 0;
        double Tsmin = 0;
        // TODO: 18/09/2018 Recalculate As minimum 
        Tsmin = Cc + Cs - Tc;
        Asmin = Tsmin / fy;

        this.minimumSteelTensionArea = Asmin;
        this.curvatureAfterCracking = ⲉc / kd;
        return analysis;
    }

    /**
     * Analyze the capacity of beam with given section and reinforcements.
     * @param sd Stress distribution type.
     * @return BeamAnalysisResult
     */
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
            int iterator = BeamContants.COMPRESSION_SOLID_DY_ITERATION;                                            // Divide kd by this number
            double b;                                                    // Distance from neutral axis and corresponding beam width
            double dy;                                                      // Strip for integration
            while (AsCalc < As) {
                Cs = 0;
                Cc = compressionSolidVolumeParabolic(fcPrime,
                        kd,
                        ⲉcu,
                        highestElev);
                fs = ⲉcu * Es * (d - kd) / kd;
                fs = calculateFs(fs, fy);

                fsPrime = fs * (kd - dPrime) / (d - kd);
                fsPrime = calculateFs(fsPrime, fy);

                Cs += AsPrime * fsPrime;

                AsCalc = (Cc + Cs) / fs;
                kd += 0.001;
            }

            double My = 0;
            double compressionSolid = Cc;
            double compressionSteel = Cs;
            double yBar;                                                    // Centroid of compression solid from top
            double[] compressionStripComponent;
            dy = kd / iterator;                                             // Reset dy
            for (int i = iterator; i > 0; i--) {
                compressionStripComponent = beamCompressionStripParabolic(i, dy, ⲉcu, kd, fcPrime, highestElev);
                fc = compressionStripComponent[0];
                b = compressionStripComponent[1];
                Cc = fc * b * dy;

                My += Cc * (kd - i * dy);
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
                fs = calculateFs(fs, fy);

                kdY = Calculators.highestY(nodes) - a;
                compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);

                Cc = fc * compressionArea;

                fsPrime = fs * (kd - dPrime) / (d - kd);
                fsPrime = calculateFs(fsPrime, fy);

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
        double fy = this.beamSection.getFy(),
                AsPrime = this.beamSection.getSteelCompression().getTotalArea(Unit.METRIC),
                fsPrime,
                Cc,
                Cs,
                fcPrime = this.beamSection.getFcPrime(),
                fc = 0.85 * fcPrime,
                kdY,
                compressionArea;
        double kd;
        double Asb = 0;
        double highestElev = Calculators.highestY(nodes);

        kd = ⲉcu * Es * d / (fy + ⲉcu * Es);

        if (sd == StressDistribution.PARABOLIC) {
            Cc = compressionSolidVolumeParabolic(fcPrime,
                    kd,
                    ⲉcu,
                    highestElev);
            fsPrime = ⲉcu * Es * (kd - dPrime) / kd;
            fsPrime = calculateFs(fsPrime, fy);
            Cs = AsPrime * fsPrime;
        } else {
            // Whitney stress block
            double beta = calculateBeta(fcPrime);
            double a;
            a = beta * kd;
            kdY = Calculators.highestY(nodes) - a;
            compressionArea = Calculators.getAreaAboveAxis(kdY, nodes);
            Cc = fc * compressionArea;
            fsPrime = ⲉcu * Es * (kd - dPrime) / kd;
            fsPrime = calculateFs(fsPrime, fy);
            Cs = AsPrime * fsPrime;
        }
        Asb = (Cc + Cs) / fy;
        this.balacedSteelTension = Asb;

        result.setCurvatureC(ⲉcu / kd);
        result.setKd(kd);
        return result;
    }

    // TODO: 18/09/2018 concrete yield analysis

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }

    /**
     * Whitney stress block beta calculator.
     * @param fcPrime Concrete compressive strength.
     * @return beta
     */
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

    /**
     * Concrete compression solid magnitude.
     * @param fcPrime concrete compressive strength
     * @param kd trial or value of height of compression block
     * @param ⲉcu maximum concrete strain
     * @param highestElev top elevation of beam section
     * @return Cc
     */
    private double compressionSolidVolumeParabolic(double fcPrime,
                                                   double kd,
                                                   double ⲉcu,
                                                   double highestElev)
    {
        List<BeamSectionNode> nodes = this.beamSection.getSection();
        double fc, b, Cc = 0;
        int iterator = BeamContants.COMPRESSION_SOLID_DY_ITERATION;
        double dy = kd / iterator;          // Strip height
        double[] compressionStripComponent;
        for (int i = iterator; i > 0; i--) {
            compressionStripComponent = beamCompressionStripParabolic(i, dy, ⲉcu, kd, fcPrime, highestElev);
            fc = compressionStripComponent[0];
            b = compressionStripComponent[1];
            Cc += fc * b * dy;
        }
        return Cc;
    }

    private double compressionSolidVolumeTriangular(double kd,
                                                    double highestElev,
                                                    double h,
                                                    double fr)
    {
        List<BeamSectionNode> nodes = this.beamSection.getSection();
        double fc, b, Cc = 0;
        int iterator = BeamContants.COMPRESSION_SOLID_DY_ITERATION;
        double dy = kd / iterator;          // Strip height
        double[] compressionStripComponent;
        for (int i = iterator; i > 0; i--) {
            compressionStripComponent = beamCompressionStripTriangular(i, dy, kd, h, fr, highestElev);
            fc = compressionStripComponent[0];
            b = compressionStripComponent[1];
            Cc += fc * b * dy;
        }
        return Cc;
    }

    /**
     * Beam compression solid strip.
     * @param i ith strip for integration
     * @param dy height of strip
     * @param ⲉcu maximum concrete strain
     * @param kd trial or value of height of compression block
     * @param fcPrime concrete compressive strength
     * @param highestElev top elevation of beam section
     * @return Array consisting of b(y) and fc(y)
     */
    private double[] beamCompressionStripParabolic(int i,
                                                   double dy,
                                                   double ⲉcu,
                                                   double kd,
                                                   double fcPrime,
                                                   double highestElev) {
        double[] result = new double[2];
        double y = i * dy;
        double ⲉcy = ⲉcu * y / kd;             // Strain at y
        double fc = 0.85 * fcPrime * (2 * ⲉcy / ⲉcu - Math.pow((ⲉcy / ⲉcu), 2));
        double yElev = highestElev - kd + y;
        double b = Calculators.getBaseAtY(yElev, this.beamSection.getSection());
        result[0] = fc;
        result[1] = b;
        return result;
    }

    /**
     * Beam compression solid strip
     * @param i ith strip for integration
     * @param dy height of strip
     * @param kd trial or value of height of compression block
     * @param h total beam height
     * @param fr modulus of fructure of concrete
     * @param highestElev top elevation of beam section
     * @return Array consisting of b(y) and fc(y)
     */
    private double[] beamCompressionStripTriangular(int i,
                                                    double dy,
                                                    double kd,
                                                    double h,
                                                    double fr,
                                                    double highestElev) {
        double[] result = new double[2];

        double y = i * dy;
        double fc = fr * (h - kd) / kd;
        double fcy = fc * y / kd;
        double yElev = highestElev - kd + y;
        double by = Calculators.getBaseAtY(yElev, this.beamSection.getSection());

        result[0] = fcy;
        result[1] = by;

        return result;
    }

    /**
     * Returns the appropriate value of fs to be used.
     * @param fs Calculated fs
     * @param fy Steel yield strength
     * @return fs
     */
    private double calculateFs(double fs, double fy) {
        if (fs > fy) {
            return fy;
        }
        return fs;
    }
}
