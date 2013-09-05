package org.onexus.website.api.pages.search;

import org.onexus.website.api.utils.visible.IVisible;

import java.io.Serializable;

public abstract class FigureConfig implements Serializable, IVisible {

    private String title;

    private String description;

    private SearchLink link;

    private String visible;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public SearchLink getLink() {
        return link;
    }

    public void setLink(SearchLink link) {
        this.link = link;
    }
}
