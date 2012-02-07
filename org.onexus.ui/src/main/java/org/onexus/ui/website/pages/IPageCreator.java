package org.onexus.ui.website.pages;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.IWebsiteCreator;


public interface IPageCreator extends IWebsiteCreator<PageConfig, PageStatus> {
    
    public Page<?,?> create(String componentId, PageConfig config, IModel<PageStatus> statusModel);

}
