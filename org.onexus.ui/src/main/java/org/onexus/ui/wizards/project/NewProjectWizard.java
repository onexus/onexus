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
package org.onexus.ui.wizards.project;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;
import org.onexus.ui.wizards.AbstractNewResourceWizard;

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
