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
package org.onexus.website.api.utils.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.website.api.Connection;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteSession;
import org.onexus.website.api.WebsiteStatus;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.events.EventTabSelected;
import org.onexus.website.api.events.EventViewChange;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

public class ConnectionsPanel extends EventPanel {

    private List<Connection> connections;

    @Inject
    private IResourceManager resourceManager;

    public ConnectionsPanel(String id, List<Connection> connections) {
        super(id);

        onEventFireUpdate(EventQueryUpdate.class, EventTabSelected.class, EventViewChange.class);

        if (connections == null) {
            this.connections = Collections.emptyList();
        } else {
            this.connections = connections;
        }

        setVisible(!this.connections.isEmpty() && Boolean.parseBoolean(System.getProperty("org.onexus.website.connections", "false")));

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        String urlPath = "";
        Website website = findParent(Website.class);
        if (!connections.isEmpty()) {
            PageParameters params = new PageParameters();

            if (website != null) {
                WebsiteStatus status = website.getStatus();
                status.encodeParameters(params, true);
            }

            Url url = getRequestCycle().mapUrlFor(getPage().getClass(), params);
            urlPath = url.getPath() + url.getQueryString();

            int firstSlash = urlPath.indexOf('/');
            urlPath = urlPath.substring(firstSlash);
        }

        RepeatingView connectionsView = new RepeatingView("projects");
        for (Connection connection : connections) {
            WebMarkupContainer connectionItem = new WebMarkupContainer(connectionsView.newChildId());
            if (connection.getActive() != null && connection.getActive()) {
                connectionItem.add(new AttributeModifier("class", "active"));
            }
            ExternalLink link = new ExternalLink("url", connection.getUrl() + urlPath);
            link.add(new Label("title", connection.getTitle()));
            connectionItem.add(link);
            connectionsView.add(connectionItem);
        }
        addOrReplace(connectionsView);

        WebMarkupContainer divider = new WebMarkupContainer("divider");
        addOrReplace(divider);
        RepeatingView userProjects = new RepeatingView("user");
        addOrReplace(userProjects);

        // Add private projects
        divider.setVisible(false);
        if (WebsiteSession.get().isSignedIn()) {

            List<Project> projects = resourceManager.getProjects();

            for (Project project : projects) {

                String projectUrl = project.getURL();
                String userName = LoginContext.get().getUserName();

                if (projectUrl.startsWith("private://" + userName)) {

                    WebMarkupContainer connectionItem = new WebMarkupContainer(userProjects.newChildId());
                    if (website.getConfig().getORI().getProjectUrl().equals(projectUrl)) {
                        connectionItem.add(new AttributeModifier("class", "active"));
                    }

                    String projectName = project.getName();
                    String projectTitle = projectName.substring(projectName.indexOf('/') + 1);

                    ExternalLink link = new ExternalLink("url", "/web/" + projectName + "/v01" + urlPath);
                    link.add(new Label("title", projectTitle));
                    connectionItem.add(link);
                    userProjects.add(connectionItem);
                    divider.setVisible(true);
                }


            }

        }

    }

}

