package com.structuralengineering.rcbeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the various reinforced concrete beam analysis
 */
public class BeamAnalyses {
  private RCBeam rcBeam;

  /**
   * Constructor that provides the beam to be analyzed
   * @param rcb RCBeam object
   */
  public BeamAnalyses(RCBeam rcb) {
    this.rcBeam = rcb;
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

}
