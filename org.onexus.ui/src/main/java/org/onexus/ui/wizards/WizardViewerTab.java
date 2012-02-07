package org.onexus.ui.wizards;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public class WizardViewerTab implements IViewerCreator {
    
    @Override
    public String getTitle() {
	return "Wizards";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new WizardViewerTabPanel(containerId, model);
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return true;
    }

    @Override
    public double getOrder() {
	return 1;
    }

}
