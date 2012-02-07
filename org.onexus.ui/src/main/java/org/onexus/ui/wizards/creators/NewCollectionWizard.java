package org.onexus.ui.wizards.creators;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Resource;

public class NewCollectionWizard extends AbstractNewResourceWizard<Collection> {

   
    public NewCollectionWizard(String id, IModel<? extends Resource> resourceModel) {
	super(id, resourceModel);
	
	WizardModel model = new WizardModel();
	model.add(new ResourceName());

	init(model);
    }
    
    @Override
    protected Collection getDefaultResource() {
	return new Collection();
    }

    private final class ResourceName extends WizardStep {

	public ResourceName() {
	    super("New collection", "Creates a new collection inside the current release");
	    
	    add(getFieldResourceName());

	}
    }

    

}
