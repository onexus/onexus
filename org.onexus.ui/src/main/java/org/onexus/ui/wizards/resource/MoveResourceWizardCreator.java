package org.onexus.ui.wizards.resource;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Workspace;
import org.onexus.ui.IWizardCreator;

public class MoveResourceWizardCreator implements IWizardCreator {

    @Override
    public String getLabel() {
        return "Move";
    }

    @Override
    public String getTitle() {
        return "Move or rename a resource and all the inner resources.";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
        return new MoveResourceWizard(containerId, model);
    }

    @Override
    public double getOrder() {
        return 1;
    }

    @Override
    public boolean isVisible(Resource resource) {
        return !Workspace.class.isAssignableFrom(resource.getClass());
    }
}
