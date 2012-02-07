package org.onexus.ui.website.pages.browser;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.pages.AbstractPageCreator;
import org.onexus.ui.website.pages.Page;

public class BrowserPageCreator extends AbstractPageCreator<BrowserPageConfig, BrowserPageStatus> {

    public BrowserPageCreator() {
	super(BrowserPageConfig.class, "fixed-browser", "A collection browser");
    }

    @Override
    protected Page<?, ?> build(String componentId, BrowserPageConfig config, IModel<BrowserPageStatus> statusModel) {
	return new BrowserPage(componentId, config, statusModel);
    }

 
}
