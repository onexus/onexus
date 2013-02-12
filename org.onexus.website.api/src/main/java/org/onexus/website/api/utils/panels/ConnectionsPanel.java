package org.onexus.website.api.utils.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.website.api.Connection;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteSession;
import org.onexus.website.api.WebsiteStatus;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.events.EventQueryUpdate;

import java.util.Collections;
import java.util.List;

public class ConnectionsPanel extends EventPanel {

    private List<Connection> connections;

    public ConnectionsPanel(String id, List<Connection> connections) {
        super(id);

        onEventFireUpdate(EventQueryUpdate.class);

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

        //TODO Use an user projects manager
        if (WebsiteSession.get().getRoles().hasRole("tester")) {
            WebMarkupContainer connectionItem = new WebMarkupContainer(userProjects.newChildId());
            if (website.getConfig().getORI().getProjectUrl().endsWith("snp_hopkins_breast")) {
                connectionItem.add(new AttributeModifier("class", "active"));
            }
            ExternalLink link = new ExternalLink("url", "/web/snp_hopkins_breast/website" + urlPath);
            link.add(new Label("title", "Breast and colorectal cancer"));
            connectionItem.add(link);
            userProjects.add(connectionItem);
        } else {
            divider.setVisible(false);
        }

    }

}

