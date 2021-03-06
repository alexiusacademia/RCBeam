package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private List<Node> mainSection;
    private List<List<Node>> clippings;
    private double area;
    private boolean hasError;
    private String errMessage;

    /**
     * Empty constructor that initializes variables.
     */
    public Section() {
        mainSection = new ArrayList<>();
        clippings = new ArrayList<>();
        hasError = false;
        errMessage = "";
    }

    public boolean setMainSection(List<Node> mainSection) {
        // Check number of nodes
        if (mainSection.size() < 3) {
            errorOccured("Invalid polygon. Too few nodes.");
            return false;
        } else {
            noError();
            this.mainSection = mainSection;
            return true;
        }
    }

    /**
     * Set list of clippings for a hollow section.
     * @param clippings Collection of collection of nodes.
     * @return true if there is no error.
     */
    public boolean setClippings(List<List<Node>> clippings) {
        for (List<Node> clipping : clippings) {
            if (clipping.size() < 3) {
                errorOccured("One of more clipping polygon has too few nodes.");
                return false;
            } else {
                noError();
            }
        }
        return true;
    }

    public boolean addClipping(List<Node> clipping) {
        if (clipping.size() < 3) {
            errorOccured("Invalid polygon. Too few nodes.");
            return false;
        } else {
            noError();
            this.clippings.add(clipping);
            return true;
        }
    }

    public boolean removeClipping(int index) {
        if (this.clippings.size() < index+1) {
            errorOccured("The index may not exist.");
            return false;
        } else {
            noError();
            this.clippings.remove(index);
            return true;
        }
    }

    public double getArea() {
        noError();
        return area;
    }

    public double getHeight() {
        noError();
        return Calculators.highestY(this.mainSection) - Calculators.lowestY(this.mainSection);
    }

    public double getNeutralAxisElevation() {
        noError();
        return Calculators.highestY(this.mainSection) - centroid();
    }

    public List<Node> getMainSection() {
        noError();
        return mainSection;
    }

    /**
     * Get the effective width of a section at a certain elevation
     * deducting all hollow polygons.
     * @param elevation Point where effective width is being looked at.
     * @return width Effective width.
     */
    public double getEffectiveWidth(double elevation) {
        double width;
        List<Node> mainSectionIntersections = new ArrayList<>();
        List<List<Node>> clippingsIntersections = new ArrayList<>();

        // Find intersections at the main section
        for (int i = 1; i < this.mainSection.size(); i++) {
            if (Calculators.hasIntersected(elevation, this.mainSection.get(i - 1), this.mainSection.get(i))) {
                Node intersection = Calculators.getIntersection(elevation,
                        this.mainSection.get(i - 1),
                        this.mainSection.get(i));
                mainSectionIntersections.add(intersection);
            }
        }
        if (mainSectionIntersections.size() != 2) {
            width = 0;
            return width;
        } else {
            // Width of the main section
            width = Calculators.distanceBetweenTwoNodes(mainSectionIntersections.get(0),
                    mainSectionIntersections.get(1));
        }

        // For intersections at clippings
        List<Node> clipIntersection = new ArrayList<>();

        // Collect all the clippings
        for (List<Node> clip : this.clippings) {
            clipIntersection.clear();
            for (int i = 1; i < clip.size(); i++) {
                if (Calculators.hasIntersected(elevation, clip.get(i - 1), clip.get(i))) {
                    Node intersection = Calculators.getIntersection(elevation,
                            clip.get(i - 1), clip.get(i));
                    clipIntersection.add(intersection);
                }
            }
            clippingsIntersections.add(clipIntersection);
        }

        // Deduct total width of holes.
        for (List<Node> clips : clippingsIntersections) {
            if (clips.size() > 2) {
                // Too much intersections. Should be limited to 2
                errorOccured("Invalid clipping polygon.");
                break;
            } else if (clips.size() == 2) {
                width -= Calculators.distanceBetweenTwoNodes(clips.get(0), clips.get(1));
            }
        }

        noError();
        return width;
    }

    /**
     * Calculates area of the section deducting all hollow sections.
     * @return area
     */
    public double grossAreaOfConcrete() {
        double area;

        // Check if mainSection is available
        if (this.mainSection.size() < 3) {
            errorOccured("Main section has invalid number of nodes or is not defined.");
            return 0;
        }

        double mainArea;
        mainArea = Calculators.calculateArea(this.mainSection);

        double clippingAreas = 0;
        for (List<Node> clip : this.clippings) {
            clippingAreas += Calculators.calculateArea(clip);
        }

        area = mainArea - clippingAreas;

        // Check for validity of main and clipping sections
        if (area < 0) {
            errorOccured("Invalid setting of section polygons.");
            return 0;
        }

        this.hasError = false;
        this.errMessage = "Success";
        this.area = area;
        return area;
    }

    /**
     * Calculates the distance of the centroid of the section from
     * the topmost node.
     * @return kd
     */
    public double centroid() {
        double kd;

        // For the main section
        double maMain = Calculators.calculateCentroidY(this.mainSection) * Calculators.calculateArea(this.mainSection);

        // For each clipping
        double highestPoint = Calculators.highestY(this.mainSection);
        double maClippings = 0;
        double clipHighestPoint;
        for (List<Node> clipping : this.clippings) {
            clipHighestPoint = Calculators.highestY(clipping);
            maClippings += Calculators.calculateArea(clipping) *
                    (Calculators.calculateCentroidY(clipping) +
                            (highestPoint - clipHighestPoint));
        }

        // Gross area
        double grossArea = grossAreaOfConcrete();
        kd = (maMain - maClippings) / grossArea;

        noError();
        return kd;
    }

    public double areaAboveAxis(double yElev) {
        double area;

        double mainSectionArea = Calculators.getAreaAboveAxis(yElev, this.mainSection);
        double clippingAreas = 0;
        for (List<Node> clipping : this.clippings) {
            clippingAreas += Calculators.getAreaAboveAxis(yElev, clipping);
        }

        area = mainSectionArea - clippingAreas;

        noError();
        return area;
    }

    public double centroidAboveAxis(double yElev) {
        double kd;

        double maMainSection, mainSectionArea, mainY;
        double maClippingSections = 0, clippingSectionAreas = 0;

        mainY = Calculators.getCentroidAboveAxis(yElev, this.mainSection);
        mainSectionArea = Calculators.getAreaAboveAxis(yElev, this.mainSection);
        maMainSection = mainY * mainSectionArea;

        for (List<Node> clipping : this.clippings) {
            double area, y;
            area = Calculators.getAreaAboveAxis(yElev, clipping);
            y = Calculators.getCentroidAboveAxis(yElev, clipping) +
                    Calculators.highestY(this.mainSection) - Calculators.highestY(clipping);
            clippingSectionAreas += area;
            maClippingSections += area * y;
        }

        kd = (maMainSection - maClippingSections) / (mainSectionArea - clippingSectionAreas);

        noError();
        return kd;
    }

    private void noError() {
        this.hasError = false;
        this.errMessage = "Success";
    }

    private void errorOccured(String msg) {
        this.hasError = true;
        this.errMessage = msg;
    }
}
