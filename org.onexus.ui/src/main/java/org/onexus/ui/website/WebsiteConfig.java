package org.onexus.ui.website;

import java.util.List;

import org.onexus.core.resources.MetadataResource;
import org.onexus.ui.website.pages.PageConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("website")
public class WebsiteConfig extends MetadataResource implements IWebsiteConfig {
    
    private WebsiteStatus defaultStatus;
    
    private List<PageConfig> pages;
    
    public WebsiteConfig() {
	super();
    }

    @Override
    public WebsiteStatus getDefaultStatus() {
	return defaultStatus;
    }

    @Override
    public WebsiteStatus createEmptyStatus() {
	return new WebsiteStatus();
    }

    public void setDefaultStatus(WebsiteStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public List<PageConfig> getPages() {
	return pages;
    }
    
    public PageConfig getPage(String pageId) {
	if (pages != null) {
	    for (PageConfig page : pages) {
		if (page.getId().equals(pageId)) {
		    return page;
		}
	    }
	}
	
	return null;
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages;
    } 

}
