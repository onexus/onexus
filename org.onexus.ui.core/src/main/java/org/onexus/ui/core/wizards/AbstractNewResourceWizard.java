/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.ui.core.wizards;

import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.core.pages.resource.ResourcesPage;

import javax.inject.Inject;

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
            String resourceUri = ResourceUtils.concatURIs(parentUri, resource.getName());
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
        final RequiredTextField<String> resourceName = new RequiredTextField<String>("resource.name");
        resourceName.setOutputMarkupId(true);
        resourceName.add(new PatternValidator("[\\w-.\\+]*"));
        resourceName.add(new DuplicatedResourceValidator());

        resourceName.add(new AjaxFormValidatingBehavior(getForm(), "onchange") {
            @Override
            protected void updateAjaxAttributes(final AjaxRequestAttributes attributes)
            {
                super.updateAjaxAttributes(attributes);

                String id = "throttle-" + resourceName.getMarkupId();
                ThrottlingSettings throttlingSettings = new ThrottlingSettings(id, Duration.seconds(1));
                 attributes.setThrottlingSettings(throttlingSettings);
            }
        });

        return resourceName;
    }

    public class DuplicatedResourceValidator extends Behavior implements IValidator<String> {

        @Override
        public void validate(IValidatable<String> validatable) {

            if (parentUri != null && resource != null && resource.getName() != null) {

                String resourceURI = ResourceUtils.concatURIs(parentUri, validatable.getValue());

                Resource test = resourceManager.load(Resource.class, resourceURI);

                if (test != null) {
                    validatable.error(new ValidationError("duplicated-resource"));
                }


            }


        }
    }

}
