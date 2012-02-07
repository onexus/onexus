package org.onexus.ui.wizards.creators;

import javax.inject.Inject;

import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.wizards.AbstractWizard;
import org.onexus.ui.workspace.pages.ResourcesPage;

public abstract class AbstractNewResourceWizard<T extends Resource> extends AbstractWizard {

    @Inject
    private IResourceManager resourceManager;
    
    private T resource;
    private String parentUri;

    public AbstractNewResourceWizard(String id, IModel<? extends Resource> resourceModel) {
	super(id);

	Resource parent = resourceModel.getObject();
	this.parentUri = (parent == null) ? null : parent.getURI();

	this.resource = getDefaultResource();

	setDefaultModel(new CompoundPropertyModel<AbstractNewResourceWizard<?>>(this));

    }

    protected abstract T getDefaultResource();

    @Override
    public void onFinish() {

	if (parentUri != null) {
	    String resourceUri = ResourceTools.concatURIs(parentUri, resource.getName());
	    resource.setURI(resourceUri);
	}

	if (resource.getURI() != null) {
	    resourceManager.save(resource);
	    PageParameters params = new PageParameters().add("uri", resource.getURI());
	    setResponsePage(ResourcesPage.class, params);
	}

	super.onFinish();
    }

    public T getResource() {
	return resource;
    }

    public void setResource(T resource) {
	this.resource = resource;
    }
    
    protected String getParentUri() {
	return this.parentUri;
    }
    
    protected RequiredTextField<String> getFieldResourceName() {
	RequiredTextField<String> resourceName = new RequiredTextField<String>("resource.name");
	resourceName.setOutputMarkupId(true);
	resourceName.add(new PatternValidator("[\\w-.\\+]*"));
	resourceName.add(new DuplicatedResourceValidator());
	
	resourceName.add(new AjaxFormValidatingBehavior(getForm(), "onchange").setThrottleDelay(Duration.seconds(1)));
	
	return resourceName;
    }
    
    public class DuplicatedResourceValidator extends AbstractValidator<String> {

	@Override
	protected void onValidate(IValidatable<String> validatable) {
	    
	    if (parentUri != null && resource != null && resource.getName() != null) {
		
		String resourceURI = ResourceTools.concatURIs(parentUri, validatable.getValue());
		
		Resource test = resourceManager.load(Resource.class, resourceURI);
		
		if (test != null) {
		    error(validatable, "duplicated-resource");
		}
		
		
	    }
	    
	    
	}
	
    }

}
