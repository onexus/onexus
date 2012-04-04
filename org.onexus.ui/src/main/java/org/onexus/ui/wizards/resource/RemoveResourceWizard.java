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
package org.onexus.ui.wizards.resource;

import org.apache.wicket.extensions.wizard.StaticContentStep;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.wizards.AbstractWizard;
import org.onexus.ui.workspace.pages.ResourcesPage;

import javax.inject.Inject;
import java.util.List;

public class RemoveResourceWizard extends AbstractWizard {

    @Inject
    private IResourceManager resourceManager;

    private String resourceURI;

    public RemoveResourceWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id);

        resourceURI = resourceModel.getObject().getURI();

        WizardModel model = new WizardModel();
        model.add(new StaticContentStep("Remove", "Delete this resource and all inner resources.",
                "<p>Are you shure that you want to <strong>PERMANENTLY REMOVE</strong></p>" + "<p><strong>" + resourceURI
                        + "</strong></p>" + "<p>and <strong>all the inner resources</strong>.</p>", true));

        init(model);
    }

    @Override
    public void onFinish() {

        removeRecursive(resourceURI, resourceManager, true);

        // Change to parent
        String parentURI = ResourceTools.getParentURI(resourceURI);

        PageParameters params = new PageParameters().add("uri", parentURI);
        setResponsePage(ResourcesPage.class, params);

    }

    private static void removeRecursive(String resourceURI, IResourceManager rm, boolean commit) {

        List<Resource> children = rm.loadChildren(Resource.class, resourceURI);

        for (Resource child : children) {
            removeRecursive(child.getURI(), rm, commit);
        }

        rm.remove(resourceURI);
        if (commit) {
            rm.commit(resourceURI);
        }
    }

}
