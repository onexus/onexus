package org.onexus.ui.website.pages.browser;


import org.onexus.ui.website.widgets.WidgetConfig;

import java.io.Serializable;
import java.util.List;

public class ViewConfig implements Serializable {

    private String title;
    private String layout;
    
    private List<WidgetConfig> widgets;

    public ViewConfig() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<WidgetConfig> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WidgetConfig> widgets) {
        this.widgets = widgets;
    }

    @Override
    public String toString() {
        return title;
    }
}
