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
            this.hasError = true;
            this.errMessage = "Invalid polygon. Too few nodes.";
            return false;
        } else {
            this.hasError = false;
            this.errMessage = "Success";
            this.mainSection = mainSection;
            return true;
        }
    }

    public boolean setClippings(List<List<Node>> clippings) {
        for (List<Node> clipping : clippings) {
            if (clipping.size() < 3) {
                this.hasError = true;
                this.errMessage = "One of more clipping polygon has too few nodes.";
                return false;
            } else {
                this.hasError = false;
                this.errMessage = "Success";
            }
        }
        return true;
    }

    public boolean addClipping(List<Node> clipping) {
        if (clipping.size() < 3) {
            this.hasError = true;
            this.errMessage = "Invalid polygon. Too few nodes.";
            return false;
        } else {
            this.hasError = false;
            this.errMessage = "Success";
            this.clippings.add(clipping);
            return true;
        }
    }

    public void removeClipping(int index) {
        this.clippings.remove(index);
    }

    public double getArea() {
        return area;
    }

    /**
     * Get the effective width of a section at a certain elevation
     * deducting all hollow polygons.
     * @param elevation Point where effective width is being looked at.
     * @return width Effective width.
     */
    public double getEffectiveWidth(int elevation) {
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

        // Width of the main section
        width = Calculators.distanceBetweenTwoNodes(mainSectionIntersections.get(0),
                mainSectionIntersections.get(1));

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

        for (List<Node> clips : clippingsIntersections) {
            if (clips.size() > 2) {
                // Too much intersections. Should be limited to 2
                this.hasError = true;
                this.errMessage = "Invalid clipping polygon.";
                break;
            } else {
                width -= Calculators.distanceBetweenTwoNodes(clips.get(0), clips.get(1));
            }
        }

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
            return 0;
        }

        double mainArea;
        mainArea = Calculators.calculateArea(this.mainSection);

        double clippingAreas = 0;
        for (List<Node> clip : this.clippings) {
            clippingAreas += Calculators.calculateArea(clip);
        }

        area = mainArea - clippingAreas;
        this.area = area;
        return area;
    }
}
