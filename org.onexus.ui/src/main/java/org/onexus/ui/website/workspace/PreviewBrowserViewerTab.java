package org.onexus.ui.website.workspace;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;
import org.onexus.ui.website.WebsiteConfig;

public class PreviewBrowserViewerTab implements IViewerCreator {
    
    @Override
    public String getTitle() {
	return "Preview";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new PreviewBrowserViewer(containerId, model);
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return WebsiteConfig.class.isAssignableFrom(resourceType);
    }

    @Override
    public double getOrder() {
	return 2;
    }

}
