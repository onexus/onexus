package org.onexus.ui.viewers;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Source;
import org.onexus.ui.IViewerCreator;

public class FilePreviewViewerTab implements IViewerCreator {


    @Override
    public String getTitle() {
        return "Preview";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
        return new FilePreviewViewer(containerId, model);
    }

    @Override
    public double getOrder() {
        return 10;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
        return Source.class.isAssignableFrom(resourceType);
    }
}
