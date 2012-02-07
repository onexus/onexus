package org.onexus.ui.wizards.creators;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public class NewWebsiteWizardCreator implements IWizardCreator {

    @Override
    public String getTitle() {
	return "New website";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new NewWebsiteWizard(containerId, model);
    }

    @Override
    public double getOrder() {
	return 1;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return Project.class.isAssignableFrom(resourceType);
    }

}
