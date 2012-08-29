package org.onexus.ui.website.widgets.icanplot;

import java.io.Serializable;

public class PlotFields implements Serializable {

    private String x;
    private String y;
    private String color;
    private String size;

    public PlotFields() {
    }

    public PlotFields(String x, String y, String color, String size) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
