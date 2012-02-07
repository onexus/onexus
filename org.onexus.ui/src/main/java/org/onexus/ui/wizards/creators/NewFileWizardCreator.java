package org.onexus.ui.wizards.creators;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Release;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public class NewFileWizardCreator implements IWizardCreator {

    @Override
    public String getTitle() {
	return "New file";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new NewFileWizard(containerId, model);
    }

    @Override
    public double getOrder() {
	return 0;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return Release.class.isAssignableFrom(resourceType);
    }

}
