package com.structuralengineering.rcbeam.analysis;

import com.structuralengineering.rcbeam.properties.BeamSection;
import com.structuralengineering.rcbeam.properties.BeamSectionNode;
import com.structuralengineering.rcbeam.properties.SteelTension;
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

  /**
   * Constructor that provides the beam section to be analyzed
   * @param bSection BeamSection
   */
  public BeamAnalyses(BeamSection bSection) {
    this.beamSection = bSection;
  }

  /**
   * ******************************************
   * Methods
   * ******************************************
   */

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
    printString("Beam nodes = " + beamSectionNodes.size());
    steelTension = beamSection.getSteelTension();
    double As = steelTension.getTotalArea(true);                  // Get the steel area in tension
    double ⲉo = beamSection.getConcreteStrainIndex();                     // ⲉo

    double At = 0;                                                        // Total area of section (Transformed)
    double n = 1;                                                         // Modular ratio

    // Calculate total area including steel transformed
    n = beamSection.getModularRatio();
    At += Ac;
    At += (n - 1) * As;

    // Calculate moments of areas
    double Ma = 0;
    Ma += (n - 1) * As * beamSection.getEffectiveDepth();
    Ma += Ac * yc;

    double kd = 0;                                                        // Neutral axis to extreme compression fiber.
    kd = Ma / At;
    double kdY = Calculators.highestY(beamSectionNodes) - kd;             // Elevation of kd.

    double ⲉc = (fr / Ec) / (h - kd) * kd;                                // Strain in concrete compression
    double fc = ⲉc * Ec;                                                  // Concrete stress
    double 𝜆o = ⲉc / ⲉo;                                                  // Ductility factor
    double k2 = 1 / 4.0 * (4 - 𝜆o) / (3 - 𝜆o);                            // Compression resultant location factor
    double Lo = solveForLo(𝜆o, true);
    double compressionArea = Calculators.getAreaAboveAxis(kdY, beamSectionNodes);

    System.out.println("n = " + n);
    System.out.println("kd = " + kd);
    System.out.println("fr = " + fr);
    System.out.println("ⲉc = " + ⲉc);
    System.out.println("fc = " + fc);
    System.out.println("\uD835\uDF06o = " + 𝜆o);
    System.out.println("k2 = " + k2);
    System.out.println("Compression area = " + compressionArea);

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
