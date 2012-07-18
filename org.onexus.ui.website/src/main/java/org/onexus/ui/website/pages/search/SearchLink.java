package org.onexus.ui.website.pages.search;

import org.onexus.ui.website.utils.visible.IVisible;

import java.io.Serializable;

public class SearchLink implements IVisible, Serializable {

    private String title;
    private String url;
    private String visible;

    public SearchLink() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}
