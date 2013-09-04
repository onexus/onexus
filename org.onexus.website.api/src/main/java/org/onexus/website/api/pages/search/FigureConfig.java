package org.onexus.website.api.pages.search;

import org.onexus.website.api.utils.visible.IVisible;

import java.io.Serializable;

public class FigureConfig implements Serializable, IVisible {

    private String title;

    private String text;

    private String visible;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}
