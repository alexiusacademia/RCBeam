package com.structuralengineering.rcbeam.utils;

import com.structuralengineering.rcbeam.properties.BeamSectionNode;

import java.util.ArrayList;
import java.util.List;

public class Calculators {
    /**
     * Calculates  the area of a given sets of points/nodes.
     *
     * @param nodes Points/vertices of the unclosed polygon.
     * @return Area of the polygon.
     */
    public static double calculateArea(List<BeamSectionNode> nodes) {
        // Add the first node to the end of points.
        List<BeamSectionNode> newNodes = new ArrayList<>();

        newNodes.addAll(nodes);

        newNodes.add(nodes.get(0));
        // Number of nodes.
        int n = newNodes.size();

        // Initialize area variable.
        double area = 0;

        int j;

        // Shoelace formula.
        for (int i = 0; i < n - 1; i++) {
            j = (i + 1) % n;
            area += newNodes.get(i).getX() * newNodes.get(j).getY();
            area -= newNodes.get(j).getX() * newNodes.get(i).getY();
        }
        area = Math.abs(area) / 2;
        return area;
    }

    /**
     * Centroid of polygon from the top
     *
     * @param nodes Polygon definition
     * @return centroid
     */
    public static double calculateCentroidY(List<BeamSectionNode> nodes) {
        double kd = 0;

        List<BeamSectionNode> newNodes = new ArrayList<>();

        newNodes.addAll(nodes);

        // Add the first node to the end of points.
        newNodes.add(nodes.get(0));

        // Number of nodes.
        int n = newNodes.size();

        double area = calculateArea(nodes);

        for (int i = 0; i < (n - 1); i++) {
            kd += (newNodes.get(i).getY() + newNodes.get(i + 1).getY()) *
                    (newNodes.get(i).getX() * newNodes.get(i + 1).getY() - newNodes.get(i + 1).getX() * newNodes.get(i).getY());
        }

        kd = Math.abs(kd / (6 * area));
        kd = Math.abs(highestY(newNodes) - kd);

        return kd;
    }

    /**
     * Get the lowest point(y) in a set of vertices.
     *
     * @param nodes Set of vertices.
     * @return Lowest y.
     */
    public static double lowestY(List<BeamSectionNode> nodes) {
        double lowest = nodes.get(0).getY();

        for (BeamSectionNode node : nodes) {
            if (node.getY() < lowest) {
                lowest = node.getY();
            }
        }

        return lowest;
    }

    /**
     * Get the highest point(y) in a set of vertices.
     *
     * @param nodes Set of vertices.
     * @return Highest y.
     */
    public static double highestY(List<BeamSectionNode> nodes) {
        double highest = nodes.get(0).getY();

        for (BeamSectionNode node : nodes) {
            if (node.getY() > highest) {
                highest = node.getY();
            }
        }

        return highest;
    }

    public static double getCentroidAboveAxis(double axisElevation, List<BeamSectionNode> nodes) {
        // Hold the new nodes
        List<BeamSectionNode> newNodes = new ArrayList<>();

        int intersected = 0;
        boolean isAbove = false;

        double x1, x2, x3;
        double y1, y2, y3;
        y2 = axisElevation;

        // Iterate to each node to look for intersection
        for (int i = 1; i < nodes.size(); i++) {
            y1 = nodes.get(i - 1).getY();
            y3 = nodes.get(i).getY();
            x1 = nodes.get(i - 1).getX();
            x3 = nodes.get(i).getX();
            x2 = (y2 - y3) / (y1 - y3) * (x1 - x3) + x3;

            if (intersected < 2) {
                if ((y1 <= axisElevation && y3 > axisElevation) ||
                        (y1 >= axisElevation && y3 < axisElevation)) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
            }
            if (nodes.get(i).getY() >= axisElevation) {
                // Add only nodes that are above or equal to the cutting axis elevation
                newNodes.add(nodes.get(i));
            }
        }

        return calculateCentroidY(newNodes);
    }

    public static double getAreaAboveAxis(double axisElevation, List<BeamSectionNode> nodes) {
        // Hold the new nodes
        List<BeamSectionNode> newNodes = new ArrayList<>();

        int intersected = 0;
        boolean isAbove = false;

        double x1, x2, x3;
        double y1, y2, y3;
        y2 = axisElevation;

        // Iterate to each node to look for intersection
        for (int i = 1; i < nodes.size(); i++) {
            y1 = nodes.get(i - 1).getY();
            y3 = nodes.get(i).getY();
            x1 = nodes.get(i - 1).getX();
            x3 = nodes.get(i).getX();
            x2 = interpolate(x1, x3, y1, y2, y3);

            if (intersected < 2) {

                if ((y1 <= axisElevation && y3 > axisElevation) ||
                        (y1 >= axisElevation && y3 < axisElevation)) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
            }
            if (nodes.get(i).getY() >= axisElevation) {
                // Add only nodes that are above or equal to the cutting axis elevation
                newNodes.add(nodes.get(i));
            }
        }

        // Now remove every node that is below the axis
        for (int i = 0; i < newNodes.size(); i++) {
            if (newNodes.get(i).getY() < axisElevation) {
                newNodes.remove(newNodes.get(i));
            }
        }

        return calculateArea(newNodes);
    }

    /**
     * Calculate beam width at y from neutral axis
     *
     * @param yElev Elevation of point of interest
     * @param nodes Beam nodes
     * @return Width
     */
    public static double getBaseAtY(double yElev, List<BeamSectionNode> nodes) {
        // Hold the new nodes
        List<BeamSectionNode> newNodes = new ArrayList<>();

        int intersected = 0;
        boolean isAbove = false;

        double x1, x2, x3;
        double y1, y2, y3;
        y2 = yElev;

        // Iterate to each node to look for intersection
        for (int i = 1; i < nodes.size(); i++) {
            if (intersected < 2) {
                y1 = nodes.get(i - 1).getY();
                y3 = nodes.get(i).getY();
                x1 = nodes.get(i - 1).getX();
                x3 = nodes.get(i).getX();
                x2 = interpolate(x1, x3, y1, y2, y3);
                if (y1 <= yElev && y3 > yElev) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
                if (y1 >= yElev && y3 < yElev) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
            }
        }

        double base = 0;
        if (newNodes.size() == 2) {
            base = Math.abs(newNodes.get(0).getX() - newNodes.get(1).getX());
        }

        return base;
    }

    private static double interpolate(double x1, double x3, double y1,
                                      double y2, double y3) {
        return (y2 - y3) / (y1 - y3) * (x1 - x3) + x3;
    }
}
