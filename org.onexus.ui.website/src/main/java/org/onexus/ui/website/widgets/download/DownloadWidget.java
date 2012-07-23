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
package org.onexus.ui.website.widgets.download;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.collection.api.query.Query;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.download.scripts.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class DownloadWidget extends Widget<DownloadWidgetConfig, DownloadWidgetStatus> {

    public final static ResourceReference CSS = new CssResourceReference(DownloadWidget.class, "prettify/prettify.css");
    public final static ResourceReference JS = new JavaScriptResourceReference(DownloadWidget.class, "prettify/prettify.js");

    public final static List<IQueryScript> scripts = Arrays.asList(new IQueryScript[]{
            new BashScript(),
            new PythonScript(),
            new PerlScript(),
            new RScript()
    });

    private String webserviceUrl;

    public DownloadWidget(String componentId, IModel<DownloadWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);

        // Get query
        final StringBuilder oql = new StringBuilder();
        Query query = getQuery();
        query.toString(oql, false);

        // Webservice URL
        ResourceReference webservice = OnexusWebApplication.get().getWebService();
        webserviceUrl = getConfig().getWebservice();

        // Try to deduce
        if (webserviceUrl == null) {
            CharSequence wsPath = urlFor(webservice, null);
            HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
            String rootUrl = request.getRequestURL().toString();
            webserviceUrl = RequestUtils.toAbsolutePath(rootUrl, wsPath.toString());
        }

        // Download as file
        String fileName = "file-" + Integer.toHexString(query.hashCode()) + ".tsv";
        PageParameters params = new PageParameters();
        params.add("query", query);
        params.add("filename", fileName);
        Link<String> link = new ResourceLink<String>("download-link", webservice, params);
        link.add(new Label("download-filename", fileName));
        add(link);

        // Add scripts
        add(new ListView<IQueryScript>("scripts", scripts) {
            @Override
            protected void populateItem(ListItem<IQueryScript> item) {

                IQueryScript script = item.getModelObject();

                // Code body
                WebMarkupContainer body = new WebMarkupContainer("body");
                body.setMarkupId(item.getMarkupId() + "-body");
                item.add(body);
                body.add(new Label("code", script.getContent(oql.toString(), webserviceUrl)).setEscapeModelStrings(false));

                // Code toggle
                Label toggle = new Label("toggle", "Use in " + script.getLabel() + " script");
                toggle.add(new AttributeModifier("href", "#" + body.getMarkupId()));
                item.add(toggle);

            }
        });



    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = (AjaxRequestTarget) event.getPayload();
            target.appendJavaScript("prettyPrint();");
        }

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(JS));
    }
}
