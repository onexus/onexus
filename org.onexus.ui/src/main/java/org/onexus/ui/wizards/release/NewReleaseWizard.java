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
package org.onexus.ui.wizards.release;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.onexus.core.resources.Release;
import org.onexus.core.resources.Resource;
import org.onexus.ui.wizards.AbstractNewResourceWizard;

public class NewReleaseWizard extends AbstractNewResourceWizard<Release> {

    private Boolean dontUseSemVer = false;

    public NewReleaseWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id, resourceModel);

        WizardModel model = new WizardModel();
        model.add(new ResourceName());

        init(model);
    }

    public Boolean getDontUseSemVer() {
        return dontUseSemVer;
    }

    public void setDontUseSemVer(Boolean dontUseSemVer) {
        this.dontUseSemVer = dontUseSemVer;
    }

    @Override
    protected Release getDefaultResource() {
        return new Release();
    }

    private static PatternValidator semanticVersion = new PatternValidator("^[0-9]+\\.[0-9]+\\.[0-9]+(\\-[\\w\\.]+)?{0,1}(\\+[\\w\\.]+)?{0,1}") {

        @Override
        protected String resourceKey() {
            return "semantic-versioning-message";
        }

    };

    private final class ResourceName extends WizardStep {

        private TextField<String> resourceName;

        public ResourceName() {
            super("New release", "Creates a new release inside the current project");

            resourceName = getFieldResourceName();
            if (!getDontUseSemVer()) {
                resourceName.add(semanticVersion);
            }
            addOrReplace(resourceName);

            CheckBox checkBox = new AjaxCheckBox("semver", new PropertyModel<Boolean>(NewReleaseWizard.this, "dontUseSemVer")) {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {

                    if (!getDontUseSemVer()) {
                        resourceName.add(semanticVersion);
                    } else {
                        resourceName.remove(semanticVersion);
                    }
                }
            };

            add(checkBox);
        }
    }


}
