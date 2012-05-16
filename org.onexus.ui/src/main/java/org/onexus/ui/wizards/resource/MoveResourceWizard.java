package org.onexus.ui.wizards.resource;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.wizards.AbstractWizard;
import org.onexus.ui.workspace.pages.ResourcesPage;

import javax.inject.Inject;
import java.util.List;

public class MoveResourceWizard extends AbstractWizard {

    @Inject
    public IResourceManager resourceManager;

    private String currentLocation;
    private String newLocation;

    public MoveResourceWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id);

        currentLocation = resourceModel.getObject().getURI();
        newLocation = currentLocation;

        WizardModel model = new WizardModel();
        model.add(new NewLocation());
        init(model);

    }

    @Override
    public void onFinish() {

        if (isValidDestionation(newLocation, resourceManager)) {
            moveRecursive(currentLocation, currentLocation, newLocation, true);

            PageParameters params = new PageParameters().add("uri", newLocation);
            setResponsePage(ResourcesPage.class, params);
        }

    }

    private void moveRecursive(String resourceUri, String fromUri, String toUri, boolean commit) {

        Resource oldResource = resourceManager.load(Resource.class, resourceUri);
        List<Resource> children = resourceManager.loadChildren(Resource.class, resourceUri);

        // Move current resource
        resourceManager.remove(resourceUri);
        String newUri = resourceUri.replace(fromUri, toUri);
        oldResource.setURI(newUri);
        oldResource.setName(ResourceUtils.getResourceName(newUri));
        resourceManager.save(oldResource);

        for (Resource child : children) {
            moveRecursive(child.getURI(), fromUri, toUri, commit);
        }

        if (commit) {
            resourceManager.commit(resourceUri);
            resourceManager.commit(newUri);
        }

    }

    private static boolean isValidDestionation(String destinationUri, IResourceManager rm) {

        // Check that the parent of the new location exists
        String parentUri = ResourceUtils.getParentURI(destinationUri);
        Resource parent = rm.load(Resource.class, parentUri);
        if (parent == null) {
            return false;
        }

        // Check that there is no resource with the same uri
        Resource duplicated = rm.load(Resource.class, destinationUri);
        if (duplicated != null) {
            return false;
        }

        return true;
    }


    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(String newLocation) {
        this.newLocation = newLocation;
    }

    private class NewLocation extends WizardStep {

        public NewLocation() {
            super("Move", "Move a resource and all the inner resources");

            add(new TextField<String>("currentLocation").setEnabled(false));

            TextField<String> newLocation = new TextField<String>("newLocation");
            newLocation.add(new UrlValidator());
            newLocation.add(new ParentResourceValidator());

            add(newLocation);


        }


    }

    private class ParentResourceValidator implements IValidator<String> {

        @Override
        public void validate(IValidatable<String> validatable) {

            String resourceUri = validatable.getValue();
            String parentUri = ResourceUtils.getParentURI(resourceUri);

            Resource parent = resourceManager.load(Resource.class, parentUri);

            if (parent == null) {
                error("Parent destination location '" + parentUri + "' don't exists");
            }

            Resource duplicated = resourceManager.load(Resource.class, resourceUri);
            if (duplicated != null) {
                error("Destination URI already exists. Remove it first.");
            }

        }
    }
}
