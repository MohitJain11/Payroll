package com.kspay.shrusthi.app.models;

public class MonthsModel {
    int id;
    String value;
    String mode;

    public MonthsModel(int id, String value, String mode) {
        this.id = id;
        this.value = value;
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getMode() {
        return mode;
    }
}
