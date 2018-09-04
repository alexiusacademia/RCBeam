package com.structuralengineering.rcbeam.utils;

import com.structuralengineering.rcbeam.properties.BeamSectionNode;

import java.util.ArrayList;
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

    public static double getAreaAboveAxis(double axisElevation, List<BeamSectionNode> nodes) {
        // Hold the new nodes
        List<BeamSectionNode> newNodes = new ArrayList<>();

        int intersected = 0;
        boolean isAbove = false;

        double x1, x2, x3;
        double y1, y2, y3;
        y2 = axisElevation;

        newNodes.add(nodes.get(0));

        // Iterate to each node to look for intersection
        for (int i = 1; i < nodes.size(); i++) {
            if (intersected < 2) {
                y1 = nodes.get(i-1).getY();
                y3 = nodes.get(i).getY();
                x1 = nodes.get(i-1).getX();
                x3 = nodes.get(i).getX();
                x2 = (y2 - y3) / (y1 - y3) * (x1 - x3) + x3;
                if (y1 <= axisElevation && y3 > axisElevation) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
                if (y1 >= axisElevation && y3 < axisElevation) {
                    // We got intersection
                    newNodes.add(new BeamSectionNode(x2, y2));
                    intersected++;
                }
            }
            newNodes.add(nodes.get(i));

        }

        // Now remove every node that is below the axis
        for (int i = 0; i < newNodes.size(); i++) {
            if (newNodes.get(i).getY() < axisElevation) {
                newNodes.remove(newNodes.get(i));
            }
        }

        return calculateArea(newNodes);
    }
}
