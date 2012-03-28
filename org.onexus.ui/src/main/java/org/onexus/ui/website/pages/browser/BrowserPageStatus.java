package org.onexus.ui.website.pages.browser;

import org.onexus.core.query.FixedEntity;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrowserPageStatus extends PageStatus {
    
    private String releaseURI;
    
    private String currentTabId;
    
    private String currentView;

    private Set<FixedEntity> fixedEntities = new HashSet<FixedEntity>();

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    public String getReleaseURI() {
        return releaseURI;
    }

    public String getCurrentTabId() {
        return currentTabId;
    }

    public void setCurrentTabId(String currentTabId) {
        if (currentTabId != null && currentTabId.equals(this.currentView = null)) {
            this.currentView = null;
        }
        this.currentTabId = currentTabId;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    public void setReleaseURI(String releaseURI) {
        this.releaseURI = releaseURI;
    }

    public Set<FixedEntity> getFixedEntities() {
        return fixedEntities;
    }

    public void setFixedEntities(Set<FixedEntity> fixedEntities) {
        this.fixedEntities = fixedEntities;
    }
}
