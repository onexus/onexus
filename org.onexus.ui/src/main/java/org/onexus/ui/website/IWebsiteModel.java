package org.onexus.ui.website;

import org.apache.wicket.model.IModel;

public interface IWebsiteModel extends IModel<WebsiteStatus> {

    public WebsiteConfig getConfig();

}
