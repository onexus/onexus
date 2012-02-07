package org.onexus.ui.website;

import org.apache.wicket.model.IModel;

public class WebsiteModel implements IModel<WebsiteStatus>, IWebsiteModel {
    
    private WebsiteConfig websiteConfig;
    private WebsiteStatus websiteStatus;
    
    public WebsiteModel(WebsiteConfig websiteConfig, WebsiteStatus websiteStatus) {
	super();
	this.websiteConfig = websiteConfig;
	this.websiteStatus = websiteStatus;
    }

    @Override
    public WebsiteStatus getObject() {
	return websiteStatus;
    }

    @Override
    public IWebsiteConfig getConfig() {
	return websiteConfig;
    }
    
    @Override
    public void setObject(WebsiteStatus object) {
	this.websiteStatus = object;	
    }

    @Override
    public void detach() {
    }

    

}
