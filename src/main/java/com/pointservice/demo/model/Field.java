package com.pointservice.demo.model;

import org.apache.poi.ss.usermodel.CellType;

import java.util.ArrayList;

/**
 * Rappresenta l'entità di una "riga" di Excel
 * i valori sono sequenziali e definiti da <code>AttributeType</code>
 */
public class Field {
    String value;
    ArrayList<String> values;

    public Field() {
        this.values = new ArrayList<>();
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void addValue(String value, Integer index) {
        this.values.add(index, value);
    }
    public void addValue(String value) {
        this.values.add(value);
    }

    public String getValue() {
        return value;
    }
}
