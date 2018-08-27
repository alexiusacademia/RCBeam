package com.structuralengineering.rcbeam.properties;

public class SteelTension {
  /**
   * Total area of steel
   * Default is in square millimeters
   */
  private double totalArea;

  /** *******************************************
   * Getters
   ******************************************* */
  public double getTotalArea(boolean inMetric) {
    if (!inMetric) {
      // Convert to english (square inches)
      return totalArea / (Math.pow(25.4, 2));
    } else {
      return totalArea;
    }
  }

  /** *******************************************
   * Setters
   ******************************************* */
  public void setTotalArea(double totalArea, boolean inMetric) {
    if (inMetric) {
      this.totalArea = totalArea;
    } else {
      this.totalArea = totalArea * Math.pow(25.4, 2);
    }
  }
}
