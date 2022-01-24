package com.example.application.models;

public class Map {
    private Long id;
    //private String identifier;
    private Double width;
    private Double height;

    public Map() {

    }

    public Map(Long id , Double width, Double height) {
        this.id = id;
        //this.identifier = identifier;
        this.width = width;
        this.height = height;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Map{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
