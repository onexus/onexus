package org.onexus.ui.website.pages.search;

import org.onexus.ui.website.pages.PageStatus;

public class SearchPageStatus extends PageStatus<SearchPageConfig> {

    private SearchType type;
    private String search;

    public SearchPageStatus() {
    }

    public SearchPageStatus(String widgetId) {
        super(widgetId);
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
