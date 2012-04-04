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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Workspace;
import org.onexus.ui.IWizardCreator;

public class NewProjectWizardCreator implements IWizardCreator {

    @Override
    public String getLabel() {
        return "New project";
    }

    @Override
    public String getTitle() {
        return "Creates a new project inside the current workspace";
    }

    @Override
    public String getDescription() {
        return "long description";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
        return new NewProjectWizard(containerId, model);
    }

    @Override
    public double getOrder() {
        return 0;
    }

    @Override
    public boolean isVisible(Resource resource) {
        return Workspace.class.isAssignableFrom(resource.getClass());
    }

}
