package org.onexus.ui.viewers;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public class EmptyPanelViewerCreator implements IViewerCreator {

    @Override
    public String getTitle() {
        return "None";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
        return new EmptyPanel(containerId);
    }

    @Override
    public double getOrder() {
        return 10000;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
        return false;
    }
}
