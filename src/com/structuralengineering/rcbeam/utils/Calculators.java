package com.structuralengineering.rcbeam.utils;

import com.structuralengineering.rcbeam.properties.Node;

import java.util.ArrayList;
import java.util.List;

public class Calculators {
    /**
     * Calculates  the area of a given sets of points/nodes.
     *
     * @param nodes Points/vertices of the unclosed polygon.
     * @return Area of the polygon.
     */
    public static double calculateArea(List<Node> nodes) {
        // Add the first node to the end of points.
        List<Node> newNodes = new ArrayList<>();

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
    public static double calculateCentroidY(List<Node> nodes) {
        double kd = 0;

        List<Node> newNodes = new ArrayList<>();

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
    public static double lowestY(List<Node> nodes) {
        double lowest = nodes.get(0).getY();

        for (Node node : nodes) {
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
    public static double highestY(List<Node> nodes) {
        double highest = nodes.get(0).getY();

        for (Node node : nodes) {
            if (node.getY() > highest) {
                highest = node.getY();
            }
        }

        return highest;
    }

    public static double getCentroidAboveAxis(double axisElevation, List<Node> nodes) {
        // Hold the new nodes
        List<Node> newNodes = new ArrayList<>();

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
                    newNodes.add(new Node(x2, y2));
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

    public static double getAreaAboveAxis(double axisElevation, List<Node> nodes) {
        // Hold the new nodes
        List<Node> newNodes = new ArrayList<>();

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
                    newNodes.add(new Node(x2, y2));
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

    public static List<Node> getNewNodes(double yElev, List<Node> nodes) {
        nodes.add(nodes.get(0));
        List<Node> newNodes = new ArrayList<>();

        newNodes.add(nodes.get(0));

        for (int i = 1; i < nodes.size(); i++) {
            newNodes.add(nodes.get(i));
            if (hasIntersected(yElev, nodes.get(i - 1), nodes.get(i))) {
                // Get the intersection point
                Node n = getIntersection(yElev, nodes.get(i - 1), nodes.get(i));
                newNodes.add(i + 1, n);
            }
        }

        // Now remove every node that is below the axis
        for (int i = 0; i < newNodes.size(); i++) {
            if (newNodes.get(i).getY() < yElev) {
                newNodes.remove(newNodes.get(i));
            }
        }

        return newNodes;
    }

    public static double getBaseAtY(double yElev, List<Node> nodes) {
        List<Node> newNodes = nodes;
        newNodes.add(nodes.get(0));

        List<Node> intersectionNodes = new ArrayList<>();

        double base = 0;

        for (int i = 1; i < newNodes.size(); i++) {
            // Check intersection
            if (hasIntersected(yElev, newNodes.get(i-1), newNodes.get(i))) {
                intersectionNodes.add(getIntersection(yElev, newNodes.get(i - 1), newNodes.get(i)));
            }
        }

        // Rearrange nodes based on abscissa
        List<Node> rearrangedNodes = sortNodesByAbscissa(intersectionNodes);

        for (int i = 0; i < rearrangedNodes.size() - 1; i += 2) {
            base += rearrangedNodes.get(i).getX() - rearrangedNodes.get(i + 1).getX();
        }

        return Math.abs(base);
    }

    public static Node getIntersection(double elevation, Node node1, Node node2) {
        double y1, y2, y3, x1, x2, x3;
        y1 = node1.getY();
        y2 = elevation;
        y3 = node2.getY();
        x1 = node1.getX();
        x3 = node2.getX();
        /*
        x2 - x1     y2 - y1
        ------- =  ---------
        x3 - x1     y3 - y1
         */
        x2 = (y2 - y1) / (y3 - y1) * (x3 - x1) + x1;

        return new Node(x2, y2);
    }

    public static Boolean hasIntersected(double elevation, Node node1, Node node2) {
        // Check if elevation is out of bounds
        double lowBound = lower(node1.getY(), node2.getY());
        double highBound = greater(node1.getY(), node2.getY());
        Boolean intersected;

        intersected = (elevation >= lowBound) && (elevation <= highBound);

        return intersected;
    }

    public static double lower(double x, double y) {
        double lower = x;
        if (y < x) {
            lower = y;
        }
        return lower;
    }

    public static double greater(double x, double y) {
        double greater = x;

        if (x < y) {
            greater = y;
        }

        return greater;
    }

    private static double interpolate(double x1, double x3, double y1,
                                      double y2, double y3) {
        return (y2 - y3) / (y1 - y3) * (x1 - x3) + x3;
    }

    private static void printString(String str) {
        System.out.println(str);
    }

    private static List<Node> sortNodesByAbscissa(List<Node> nodes) {
        // Bubble sort
        for (int i = 0; i < nodes.size() - 1; i++) {
            for (int j = 0; j < nodes.size() - 1; j++) {
                if (nodes.get(i).getX() < nodes.get(i + 1).getX()) {
                    // Swap nodes
                    Node temp = nodes.get(i);
                    nodes.set(i, nodes.get(i + 1));
                    nodes.set(i + 1, temp);
                }
            }
        }
        return nodes;
    }
}
