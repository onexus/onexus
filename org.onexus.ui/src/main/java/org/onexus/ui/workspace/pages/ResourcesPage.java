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
package org.onexus.ui.workspace.pages;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Resource;
import org.onexus.ui.workspace.events.EventResourceSelect;
import org.onexus.ui.workspace.pages.tools.ProjectSelectorTool;
import org.onexus.ui.workspace.tree.ProjectTree;
import org.onexus.ui.workspace.viewers.ViewerTabs;

import javax.inject.Inject;
import java.util.List;

public class ResourcesPage extends BasePage {

    private static final AttributeModifier ATTRIBUTE_MODIFIER_MIN = new AttributeModifier("class",
            "resources-tabs resources-tabs-min");
    private static final AttributeModifier ATTRIBUTE_MODIFIER_MAX = new AttributeModifier("class",
            "resources-tabs resources-tabs-max");
    public final static String RESOURCE = "uri";
    public final static CssResourceReference CSS = new CssResourceReference(ResourcesPage.class, "ResourcesPage.css");


    @Inject
    private IResourceManager resourceManager;

    private WebMarkupContainer resourcesTabs;
    private WebMarkupContainer resourceInfo;

    public ResourcesPage(PageParameters parameters) {

        String defaultResourceURI = getDefaultProjectURI();
        String resourceURI = parameters.get(RESOURCE).toString(defaultResourceURI);

        IModel<Resource> currentResource = new ResourceModel(resourceURI);

        add(new ProjectTree("tree", currentResource));

        //add(new DefaultNestedTree<Resource>("tree"));

        resourcesTabs = new WebMarkupContainer("resourcesTabs");
        resourcesTabs.setOutputMarkupId(true);

        resourceInfo = new WebMarkupContainer("resourceInfo");
        resourceInfo.setOutputMarkupId(true);
        resourceInfo.add(new Label("resourceType", new PropertyModel<String>(currentResource, "class.simpleName")));
        resourceInfo.add(new Label("resourceUri", new PropertyModel<String>(currentResource, "URI")));
        resourcesTabs.add(resourceInfo);

        AjaxLink<Boolean> fullscreenLink = new AjaxLink<Boolean>("fullscreen", Model.of(Boolean.FALSE)) {

            @Override
            public void onClick(AjaxRequestTarget target) {

                Boolean fullscreen = !getModelObject();

                if (fullscreen) {
                    resourcesTabs.add(ATTRIBUTE_MODIFIER_MAX);
                    addOrReplace(new Label("label", ">>"));
                } else {
                    resourcesTabs.add(ATTRIBUTE_MODIFIER_MIN);
                    addOrReplace(new Label("label", "<<").setEscapeModelStrings(true));
                }

                setModelObject(fullscreen);
                target.add(resourcesTabs);

            }

        };

        fullscreenLink.add(new Label("label", "<<"));

        resourceInfo.add(fullscreenLink);

        resourcesTabs.add(new ViewerTabs("viewers", currentResource));

        add(resourcesTabs);

        addTool(new ProjectSelectorTool());

    }

    private String getDefaultProjectURI() {
        List<Project> projects = resourceManager.loadChildren(Project.class, null);

        if (projects != null && !projects.isEmpty()) {
            return projects.get(0).getURI();
        }

        return null;
    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (EventResourceSelect.EVENT == event.getPayload()) {
            RequestCycle.get().find(AjaxRequestTarget.class).add(resourceInfo);
        }

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(CSS));
    }

}
