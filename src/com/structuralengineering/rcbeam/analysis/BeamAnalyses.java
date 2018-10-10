package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.*;
import com.structuralengineering.rcbeam.utils.BeamContants;
import com.structuralengineering.rcbeam.utils.Calculators;
import com.structuralengineering.rcbeam.utils.Conversions;

import java.util.List;

/**
 * Class for the various reinforced concrete beam analyses
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

    private static void printString(String str) {
        System.out.println(str);
    }

    private static void printLine() {
        printString("= = = = = = = = = = = = = = = = = = =");
    }

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

    // = = = = = = = = = = = = = = = = = = = = = =
    //
    // Methods
    //
    // = = = = = = = = = = = = = = = = = = = = = =

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

    /**
     * Analyze the beam with the un-cracked section right before cracking.
     *
     * @return BeamAnalysisResult of the un-cracked section.
     */
    public BeamAnalysisResult uncrackedAnalysis() {
        BeamAnalysisResult analysis = new BeamAnalysisResult();
        List<Node> beamSectionNodes = this.beamSection.getSection();

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
        double n;                                                         // Modular ratio

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

        double kd;                                                        // Neutral axis to extreme compression fiber.
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
        double ycc, yct;                                                      // Location of Cc and Tc

        Cc = compressionSolidVolumeTriangular(kd, highestElev, fc);
        double[] compressionStrip, tensionStrip;
        double dy = kd / 10000000;
        double Ccy, Tcy, Myc = 0, Myt = 0;

        // Solving for location of Cc from the top
        for (int i = 10000000; i > 0; i--) {
            compressionStrip = beamCompressionStripTriangular(i, dy, kd, fc, highestElev);
            Ccy = compressionStrip[0] * compressionStrip[1] * dy;
            Myc += Ccy * (kd - i * dy);
        }
        ycc = Myc / Cc;

        Tc = tensionSolidVolumeTriangular(h - kd, kd, highestElev, fr);

        // Solve for location of Tc
        for (int i = 10000000; i > 0; i--) {
            tensionStrip = beamTensionStripTriangular(i, dy, kd, h - kd, fr, highestElev);
            Tcy = tensionStrip[0] * tensionStrip[1] * dy;
            Myt += Tcy * (h - kd - i * dy);
        }
        yct = Myt / Tc;

        Cs = AsPrime * fsPrime;                                               // Compression force on steel
        Ts = As * fs;                                                         // Tensile force at steel

        double yCompression = (Cc * ycc + Cs * dPrime) / (Cc + Cs);             // Location of resultant of both Cc and Cs

        double Mcr = Ts * (d - yCompression) + Tc * (h - yCompression - yct);

        // Calculate minimum steel using Whitney
        double McrTrial = 0, a = 0.001, yA, yTop = 0;
        while (McrTrial < Mcr) {
            yA = highestElev - a;
            compressionArea = Calculators.getAreaAboveAxis(yA, beamSectionNodes);
            yTop = Calculators.getCentroidAboveAxis(yA, beamSectionNodes);
            McrTrial = 0.85 * this.beamSection.getFcPrime() * compressionArea * (d - yTop);
            a += 0.001;
        }
        this.crackingMoment = Mcr;

        this.minimumSteelTensionArea = Mcr / (fy * (d - yTop));

        double curvature = ⲉc / kd;

        analysis.setMomentC(Mcr);
        analysis.setCurvatureC(curvature);
        analysis.setKd(kd);

        return analysis;
    }

    // TODO: 18/09/2018 concrete yield analysis

    /**
     * Analyze the capacity of beam with given section and reinforcements.
     *
     * @param sd Stress distribution type.
     * @return BeamAnalysisResult
     */
    public BeamAnalysisResult beamCapacityAnalysis(StressDistribution sd) {
        BeamAnalysisResult analysis = new BeamAnalysisResult();

        List<Node> nodes = this.beamSection.getSection();
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

        double moment;
        double AsCalc = 0;
        double kd = 0.1;
        double highestElev = Calculators.highestY(nodes);

        if (sd == StressDistribution.PARABOLIC) {
            // Find kd
            int iterator = BeamContants.COMPRESSION_SOLID_DY_ITERATION;                                            // Divide kd by this number
            double b;                                                    // Distance from neutral axis and corresponding beam width
            double dy;                                                      // Strip for integration
            double kdIterator;
            if (this.beamSection.getUnit() == Unit.ENGLISH) {
                kdIterator = 1 / 25.4;
            } else {
                kdIterator = 1;
            }
            while (Math.abs(AsCalc - As) > 0.01 * As) {// AsCalc < As) {
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

                if (AsCalc > As) {
                    while (AsCalc > As) {
                        kd -= kdIterator;
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
                        kdIterator *= 0.5;
                    }
                } else {
                    kdIterator *= 1.5;
                }

                kd += kdIterator;
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
     *
     * @param sd Stress distribution block
     * @return analysis
     */
    public BeamAnalysisResult balancedAnalysis(StressDistribution sd) {
        BeamAnalysisResult result = new BeamAnalysisResult();

        List<Node> nodes = this.beamSection.getSection();
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
        double Asb;
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

        // Get the location of resultant concrete compression
        int iterator = BeamContants.COMPRESSION_SOLID_DY_ITERATION;
        double yBar;                                                    // Centroid of compression solid from top
        double[] compressionStripComponent;
        double dy = kd / iterator;                                             // Reset dy
        double My = 0;
        double fc1, b1;
        for (int i = iterator; i > 0; i--) {
            compressionStripComponent = beamCompressionStripParabolic(i, dy, ⲉcu, kd, fcPrime, highestElev);
            fc1 = compressionStripComponent[0];
            b1 = compressionStripComponent[1];
            Cc = fc1 * b1 * dy;

            My += Cc * (kd - i * dy);
        }
        yBar = My / Cc;

        double momentBalance = Cc * (d - yBar) + Cs * (d - dPrime);

        this.balacedSteelTension = Asb;

        result.setCurvatureC(ⲉcu / kd);
        result.setKd(kd);
        result.setMomentC(momentBalance);

        return result;
    }

    /**
     * Whitney stress block beta calculator.
     *
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
     *
     * @param fcPrime     concrete compressive strength
     * @param kd          trial or value of height of compression block
     * @param ⲉcu         maximum concrete strain
     * @param highestElev top elevation of beam section
     * @return Cc
     */
    private double compressionSolidVolumeParabolic(double fcPrime,
                                                   double kd,
                                                   double ⲉcu,
                                                   double highestElev) {
        List<Node> nodes = this.beamSection.getSection();
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

    /**
     * Calculates the volume of compression solid for uncrack beam section
     *
     * @param kd          Height of neutral axis.
     * @param highestElev Elevation of top of beam.
     * @param fc          Stress at extreme compression fiber.
     * @return Compression solid volume.
     */
    private double compressionSolidVolumeTriangular(double kd,
                                                    double highestElev,
                                                    double fc) {
        List<Node> nodes = this.beamSection.getSection();
        double fcy, b, Cc = 0;
        int iterator = 10000000;
        double dy = kd / iterator;          // Strip height
        double[] compressionStripComponent;
        for (int i = iterator; i > 0; i--) {
            compressionStripComponent = beamCompressionStripTriangular(i, dy, kd, fc, highestElev);
            fcy = compressionStripComponent[0];
            b = compressionStripComponent[1];
            Cc += fcy * b * dy;
        }
        return Cc;
    }

    private double tensionSolidVolumeTriangular(double z,
                                                double kd,
                                                double highestElev,
                                                double fr) {

        List<Node> nodes = this.beamSection.getSection();
        double fry, by, Tc = 0;
        int iterator = 10000000;
        double dy = z / iterator;          // Strip height
        double[] tensionStripComponent;
        for (int i = iterator; i > 0; i--) {
            tensionStripComponent = beamTensionStripTriangular(i, dy, kd, z, fr, highestElev);
            fry = tensionStripComponent[0];
            by = tensionStripComponent[1];
            Tc += fry * by * dy;
        }
        return Tc;
    }

    /**
     * Beam compression solid strip.
     *
     * @param i           ith strip for integration
     * @param dy          height of strip
     * @param ⲉcu         maximum concrete strain
     * @param kd          trial or value of height of compression block
     * @param fcPrime     concrete compressive strength
     * @param highestElev top elevation of beam section
     * @return Array consisting of b(y) and fc(y)
     */
    private double[] beamCompressionStripParabolic(int i,
                                                   double dy,
                                                   double ⲉcu,
                                                   double kd,
                                                   double fcPrime,
                                                   double highestElev) {
        double ⲉco = 2 * 0.85 * fcPrime / (4700 * Math.sqrt(fcPrime));
        double[] result = new double[2];
        double y = i * dy;
        double ⲉcy = ⲉcu * y / kd;             // Strain at y
        double fc;
        if (ⲉcy < ⲉco) {
            fc = 0.85 * fcPrime * (2 * ⲉcy / ⲉco - Math.pow((ⲉcy / ⲉco), 2));
        }else {
            fc = 0.85 * fcPrime;
        }
        double yElev = highestElev - kd + y;
        double b = Calculators.getBaseAtY(yElev, this.beamSection.getSection());
        result[0] = fc;
        result[1] = b;
        return result;
    }

    /**
     * Beam compression solid strip
     *
     * @param i           ith strip for integration
     * @param dy          height of strip
     * @param kd          trial or value of height of compression block
     * @param highestElev top elevation of beam section
     * @return Array consisting of b(y) and fc(y)
     */
    private double[] beamCompressionStripTriangular(int i,
                                                    double dy,
                                                    double kd,
                                                    double fc,
                                                    double highestElev) {
        double[] result = new double[2];

        double y = i * dy;
        double fcy = fc * y / kd;
        double yElev = highestElev - kd + y;
        double by = Calculators.getBaseAtY(yElev, this.beamSection.getSection());

        result[0] = fcy;
        result[1] = by;

        return result;
    }

    private double[] beamTensionStripTriangular(int i,
                                                double dy,
                                                double kd,
                                                double z,
                                                double fr,
                                                double highestElev) {
        double[] result = new double[2];
        double y = i * dy;
        double fry = fr * y / z;
        double yElev = highestElev - kd - y;
        double by = Calculators.getBaseAtY(yElev, this.beamSection.getSection());

        result[0] = fry;
        result[1] = by;

        return result;
    }

    /**
     * Returns the appropriate value of fs to be used.
     *
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
