package com.structuralengineering.rcbeam.properties;

import com.structuralengineering.rcbeam.utils.Calculators;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private List<Node> mainSection;
    private List<List<Node>> clippings;

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

        // Collect all the clippings
        for (List<Node> clip : this.clippings) {
            for (int i = 0; i < clip.size(); i++) {
                System.out.println(clip.get(i).getX() + ", " + clip.get(i).getY());
            }
        }

        return width;
    }
}
