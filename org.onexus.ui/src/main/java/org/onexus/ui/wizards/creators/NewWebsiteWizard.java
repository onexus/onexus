package org.onexus.ui.wizards.creators;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.website.WebsiteConfig;

public class NewWebsiteWizard extends AbstractNewResourceWizard<WebsiteConfig> {

   
    public NewWebsiteWizard(String id, IModel<? extends Resource> resourceModel) {
	super(id, resourceModel);
	
	WizardModel model = new WizardModel();
	model.add(new ResourceName());

	init(model);
    }
    
    @Override
    protected WebsiteConfig getDefaultResource() {
	return new WebsiteConfig();
    }

    private final class ResourceName extends WizardStep {

	public ResourceName() {
	    super("New website", "Creates a new wesite inside the current project");
	    
	    add(getFieldResourceName());

	}
    }

    

}
