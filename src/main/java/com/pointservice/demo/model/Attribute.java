package com.pointservice.demo.model;

public class Attribute {

    private int index = -1;

    private String name;

    public Attribute(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
