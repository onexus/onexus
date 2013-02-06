package org.onexus.website.api;

import java.io.Serializable;

public class Connection implements Serializable {

    private String url;
    private String title;

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
}
