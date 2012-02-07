package org.onexus.ui.website.workspace;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public class CollectionViewerTab implements IViewerCreator {

    @Override
    public String getTitle() {
	return "Browser";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new CollectionViewer(containerId, model);
    }

    @Override
    public double getOrder() {
	return 2;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return Collection.class.isAssignableFrom(resourceType);
    }

}
