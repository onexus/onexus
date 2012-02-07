package org.onexus.ui.wizards.creators;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.onexus.core.resources.Release;
import org.onexus.core.resources.Resource;

public class NewReleaseWizard extends AbstractNewResourceWizard<Release> {

   
    public NewReleaseWizard(String id, IModel<? extends Resource> resourceModel) {
	super(id, resourceModel);
	
	WizardModel model = new WizardModel();
	model.add(new ResourceName());

	init(model);
    }
    
    @Override
    protected Release getDefaultResource() {
	return new Release();
    }

    private final class ResourceName extends WizardStep {

	public ResourceName() {
	    super("New release", "Creates a new release inside the current project");
	    
	    TextField<String> resourceName = getFieldResourceName();
	    
	    PatternValidator semanticVersion = new PatternValidator("^[0-9]+\\.[0-9]+\\.[0-9]+(\\-[\\w\\.]+)?{0,1}(\\+[\\w\\.]+)?{0,1}") {

		@Override
		protected String resourceKey() {
		    return "semantic-versioning-message";
		}
		
	    };
	    resourceName.add(semanticVersion);
	    add(resourceName);
	}
    }

    

}
