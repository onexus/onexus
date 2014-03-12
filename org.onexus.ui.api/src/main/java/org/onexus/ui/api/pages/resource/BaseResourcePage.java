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
package org.onexus.ui.api.pages.resource;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.api.OnexusWebSession;
import org.onexus.ui.api.pages.SignOutPage;
import org.onexus.ui.api.pages.resource.modals.ImportProjectModal;
import org.onexus.ui.api.pages.theme.DefaultTheme;
import org.onexus.ui.api.progressbar.ProgressBar;
import org.onexus.ui.authentication.persona.SignOutBehavior;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class BaseResourcePage extends WebPage {


    @Inject
    private IResourceManager resourceManager;

    public BaseResourcePage(IModel<Resource> resourceModel) {
        super(resourceModel);

        add(new DefaultTheme());

        add(new ProgressBar("progressbar"));

        // Select the first project if there is no selection
        if (getModelObject() == null) {
            List<Project> projects = getResourceManager().getProjects();
            if (projects != null && !projects.isEmpty()) {
                getModel().setObject(projects.get(0));
            }
        }

        Link<String> link = new Link<String>("account-details") {
            @Override
            public void onClick() {

            }
        };
        link.add(new Label("username", LoginContext.get().getUserName()));
        add(link);

        if (OnexusWebApplication.get().usePersonSignIn()) {
            add(new WebMarkupContainer("signout").add(new SignOutBehavior()));
        } else {
            add(new BookmarkablePageLink<String>("signout", SignOutPage.class));
        }

        WebMarkupContainer menuProjects = new WebMarkupContainer("menuProjects");

        List<Project> projects = getResourceManager().getProjects();
        menuProjects.add(new ListView<Project>("projects", projects) {

            @Override
            protected void populateItem(ListItem<Project> item) {

                Project project = item.getModelObject();
                PageParameters parameters = new PageParameters();

                if (project != null) {

                    ORI projectURI = project.getORI();
                    parameters.set(ResourcesPage.PARAMETER_RESOURCE, projectURI);
                    Link<ResourcesPage> link = new BookmarkablePageLink<ResourcesPage>("link", ResourcesPage.class, parameters);
                    link.add(new Label("label", project.getURL()));
                    item.add(link);

                    Resource currentResource = BaseResourcePage.this.getModelObject();
                    if (currentResource != null
                            && projectURI.equals(currentResource.getORI())) {
                        item.add(new AttributeAppender("class", " active"));
                    }


                } else {
                    item.setVisible(false);
                }


            }

        });

        // Modal panels
        add(new ImportProjectModal("importProject"));


        add(menuProjects);

        if (ResourcesPage.class.isAssignableFrom(getClass())) {
            menuProjects.add(new AttributeModifier("class", "dropdown active"));
        } else {
            menuProjects.add(new AttributeModifier("class", "dropdown"));
        }

    }

    @Override
    protected void onBeforeRender() {

        if (!OnexusWebSession.get().isSignedIn()) {
            OnexusWebApplication.get().restartResponseAtSignInPage();
        }

        super.onBeforeRender();
    }

    public IModel<Resource> getModel() {
        return (IModel<Resource>) getDefaultModel();
    }

    public Resource getModelObject() {
        return (Resource) getDefaultModelObject();
    }

    protected IResourceManager getResourceManager() {
        return resourceManager;
    }


}
