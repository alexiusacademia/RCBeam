package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private List<Node> mainSection;
    private List<List<Node>> clippings;
    private boolean hasError;
    private String errMessage;

    public Section() {
        mainSection = new ArrayList<>();
        clippings = new ArrayList<>();
    }

    public void setMainSection(List<Node> mainSection) {
        this.mainSection = mainSection;
    }

    public void setClippings(List<List<Node>> clippings) {
        this.clippings = clippings;
    }

    public void addClipping(List<Node> clipping) {
        this.clippings.add(clipping);
    }

    public void removeClipping(int index) {
        this.clippings.remove(index);
    }

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
}
