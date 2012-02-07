package org.onexus.ui.wizards.creators;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public class NewReleaseWizardCreator implements IWizardCreator {

    @Override
    public String getTitle() {
	return "New release";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new NewReleaseWizard(containerId, model);
    }

    @Override
    public double getOrder() {
	return 0;
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return Project.class.isAssignableFrom(resourceType);
    }

}
