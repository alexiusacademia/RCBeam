package com.structuralengineering.rcbeam.utils;

import com.structuralengineering.rcbeam.properties.BeamSectionNode;

import java.util.List;

public class Calculators {
    /**
     * Calculates  the area of a given sets of points/nodes.
     * @param nodes Points/vertices of the unclosed polygon.
     * @return Area of the polygon.
     */
    public static double calculateArea(List<BeamSectionNode> nodes) {
        // Add the first node to the end of points.
        nodes.add(nodes.get(0));

        // Number of nodes.
        int n = nodes.size();

        // Initialize area variable.
        double area = 0;

        int j;

        // Shoelace formula.
        for (int i = 0; i < n; i++) {
            j = (i + 1) % n;
            area += nodes.get(i).getX() * nodes.get(j).getY();
            area -= nodes.get(j).getX() * nodes.get(i).getY();
        }
        area = Math.abs(area) / 2;
        return area;
    }

    public static double calculateCentroidY(List<BeamSectionNode> nodes) {
        double kd = 0;

        // Add the first node to the end of points.
        nodes.add(nodes.get(0));

        // Number of nodes.
        int n = nodes.size();

        double area = calculateArea(nodes);

        double height = Math.abs(highestY(nodes) - lowestY(nodes));

        int j;
        for (int i = 0; i < n; i++) {
            j = (i + 1) % n;
            kd += (nodes.get(i).getY() + nodes.get(i + 1).getY()) *
                    (nodes.get(i).getX() * nodes.get(i+1).getY() - nodes.get(i+1).getX() * nodes.get(i).getY());
        }

        kd = Math.abs(kd / (6 * area));
        kd = Math.abs(height - kd);
        return kd;
    }

    /**
     * Get the lowest point(y) in a set of vertices.
     * @param nodes Set of vertices.
     * @return Lowest y.
     */
    private static double lowestY(List<BeamSectionNode> nodes) {
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
     * @param nodes Set of vertices.
     * @return Highest y.
     */
    private static double highestY(List<BeamSectionNode> nodes) {
        double highest = nodes.get(0).getY();

        for (BeamSectionNode node : nodes) {
            if (node.getY() > highest) {
                highest = node.getY();
            }
        }

        return highest;
    }
}
