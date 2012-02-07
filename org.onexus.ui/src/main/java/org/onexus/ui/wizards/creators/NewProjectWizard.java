package org.onexus.ui.wizards.creators;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;

public class NewProjectWizard extends AbstractNewResourceWizard<Project> {

   
    public NewProjectWizard(String id, IModel<? extends Resource> resourceModel) {
	super(id, resourceModel);
	
	WizardModel model = new WizardModel();
	model.add(new ResourceName());

	init(model);
    }
    
    @Override
    protected Project getDefaultResource() {
	return new Project();
    }

    private final class ResourceName extends WizardStep {

	public ResourceName() {
	    super("New project", "Creates a new project inside the current workspace");
	    
	    add(getFieldResourceName());

	}
    }

    

}
