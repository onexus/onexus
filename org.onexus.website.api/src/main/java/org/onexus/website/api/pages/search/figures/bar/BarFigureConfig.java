package org.onexus.website.api.pages.search.figures.bar;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.website.api.pages.search.FigureConfig;

@XStreamAlias("figure-bar")
public class BarFigureConfig extends FigureConfig {

    private CollectionField value;

    @XStreamAlias("x-axis")
    private CollectionField xAxis;

    @XStreamAlias("y-axis")
    private CollectionField yAxis;

    private String init;

    public BarFigureConfig() {
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public CollectionField getValue() {
        return value;
    }

    public void setValue(CollectionField value) {
        this.value = value;
    }

    public CollectionField getxAxis() {
        return xAxis;
    }

    public void setxAxis(CollectionField xAxis) {
        this.xAxis = xAxis;
    }

    public CollectionField getyAxis() {
        return yAxis;
    }

    public void setyAxis(CollectionField yAxis) {
        this.yAxis = yAxis;
    }
}
