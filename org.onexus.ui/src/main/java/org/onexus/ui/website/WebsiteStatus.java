package org.onexus.ui.website;

import java.util.HashSet;
import java.util.Set;

import org.onexus.ui.website.pages.PageStatus;

public class WebsiteStatus implements IWebsiteStatus {
    
    private String currentPageId;
    private Set<PageStatus> pageStatus;
    
    public PageStatus getPageStatus(String id) {
	for (PageStatus status : getPageStatus()) {
	    if (status.getId().equals(id)) {
		return status;
	    }
	}
	return null;
    }
    
    
    public String getCurrentPageId() {
        return currentPageId;
    }


    public void setCurrentPageId(String currentPageId) {
        this.currentPageId = currentPageId;
    }

    public Set<PageStatus> getPageStatus() {
	if (pageStatus==null) {
	    pageStatus = new HashSet<PageStatus>();
	}
	return pageStatus;
    }

    public void setPageStatus(PageStatus pageStatus) {
	
	if (pageStatus != null) {
	    getPageStatus().add(pageStatus);
	}
    }


    public PageStatus getCurrentPageStatus() {
	return getPageStatus(getCurrentPageId());
    }

    
}
