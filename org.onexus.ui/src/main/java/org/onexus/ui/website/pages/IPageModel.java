package org.onexus.ui.website.pages;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.IWebsiteModel;

public interface IPageModel<S extends PageStatus> extends IModel<S> {

    public PageConfig getConfig();

    public IWebsiteModel getWebsiteModel();
}
