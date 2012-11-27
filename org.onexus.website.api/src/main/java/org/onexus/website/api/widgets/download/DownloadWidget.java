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
package org.onexus.website.api.widgets.download;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.util.resource.PackageResourceStream;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.download.scripts.*;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class DownloadWidget extends Widget<DownloadWidgetConfig, DownloadWidgetStatus> {

    public final static ResourceReference CSS = new CssResourceReference(DownloadWidget.class, "prettify/prettify.css");
    public final static ResourceReference JS = new JavaScriptResourceReference(DownloadWidget.class, "prettify/prettify.js");

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    public final static List<IQueryScript> scripts = Arrays.asList(new IQueryScript[]{
            new RScript(),
            new PythonScript(),
            new PerlScript(),
            new BashScript()
    });

	private final static List<String> formats = Arrays.asList( new String[] {
		    "Tabbulated text file",
			"Microsoft excel file"
	});

    private String webserviceUrl;

    public DownloadWidget(String componentId, IModel<DownloadWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);

        // Get query
        final StringBuilder oql = new StringBuilder();
        Query query = getQuery();
        query.toString(oql, true);

        // Webservice URL
        String serviceMount = collectionManager.getMount();
        webserviceUrl = WebsiteApplication.toAbsolutePath('/' + serviceMount);

        // Download form
		final Form<String> downloadForm = new Form<String>("form");
		downloadForm.setOutputMarkupId(true);
		final Model<String> format = new Model<String>(formats.get(0));
		downloadForm.add(new DropDownChoice<String>("format", format, formats));
		add(downloadForm);

		final AjaxDownloadBehavior ajaxDownloadBehavior = new AjaxDownloadBehavior() {
			@Override
			protected String getFileName() {
				String currentFormat = format.getObject();
				if (formats.get(0).equals(currentFormat)) {
					return "file-download.tsv";
				} else {
					return "file-download.xls";
				}
			}

			@Override
			protected IResourceStream getResourceStream() {
				return new PackageResourceStream(DownloadWidget.class, "DownloadWidget.html");
			}
		};
		downloadForm.add(ajaxDownloadBehavior);

		AjaxButton link = new AjaxButton("download") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ajaxDownloadBehavior.initiate(target);
				target.add(form);
			}
		};
		downloadForm.add(link);


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

	private class DownloadResourceStream extends AbstractResourceStream {

		@Override
		public InputStream getInputStream() throws ResourceStreamNotFoundException {
			return null;
		}

		@Override
		public void close() throws IOException {

		}
	}


}
