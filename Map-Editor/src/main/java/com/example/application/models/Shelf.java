package com.example.application.models;

public class Shelf {
    private Long id;
    private String identifier;
    private String orientation;
    private Double width;
    private Double height;
    private Double depth;

    public Shelf(Long id, String identifier, Double width, Double height) {
        this.id = id;
        this.identifier = identifier;
        this.width = width;
        this.height = height;
        this.depth = 220.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", depth=" + depth +
                '}';
    }
}
