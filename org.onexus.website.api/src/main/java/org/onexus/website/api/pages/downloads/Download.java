package org.onexus.website.api.pages.downloads;

import java.io.Serializable;

public class Download implements Serializable {

    private String title;
    private String description;
    private String query;

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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
