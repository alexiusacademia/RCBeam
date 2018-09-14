package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelCompression;
import com.structuralengineering.rcbeam.properties.SteelTension;
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
    double dPrime = beamSection.getSteelCompression().getdPrime();
    double fy = beamSection.getFy();

    steelTension = beamSection.getSteelTension();
    steelCompression = beamSection.getSteelCompression();
    double As = steelTension.getTotalArea(true);                  // Get the steel area in tension
    double AsPrime = steelCompression.getTotalArea(true);         // Get the steel area in compression
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
    printString("ⲉc = " + ⲉc);
    printString("fc = " + fc);
    printString("fs\' = " + fsPrime);
    printString("fs = " + fs);
    double Cc, Cs, Tc, Ts;                                                // Resultant forces
    Cc = 1 / 2.0 * fc * compressionArea;                                  // Compression force on concrete
    Cs = AsPrime * fsPrime;                                               // Compression force on steel
    Tc = 1 / 2.0 * fr * tensionArea;                                      // Tensile force on concrete
    Ts = As * fs;                                                         // Tensile force at steel

    double ycc = (Cs * dPrime + Cc * kd / 3) / (Cs + Cc);                 // Location of compression resultant

    double Mcr = Ts * (d - ycc) + Tc * (h - ycc - (h - kd)/3);
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

  /**
   * Analyze the beam right after the cracking occurs without additional loading.
   * @return BeamAnalysisResult right after cracking.
   */
  public BeamAnalysisResult afterCrackAnalysis() {
    BeamAnalysisResult analysis = new BeamAnalysisResult();

    return analysis;
  }

  /**
   * Analyze the beam when the concrete reaches a specified amount of stress in
   * compression.
   * @param fc The specified stress for the yielding of concrete.
   * @return BeamAnalysisResult for yield of concrete.
   */
  public BeamAnalysisResult concreteYieldAnalysis(double fc) {
    BeamAnalysisResult analysis = new BeamAnalysisResult();


    return analysis;
  }

  /**
   * Analyze the beam when the tension yields at a specified strength.
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
