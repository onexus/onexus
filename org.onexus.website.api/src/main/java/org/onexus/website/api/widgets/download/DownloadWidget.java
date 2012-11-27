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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.*;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.download.formats.ExcelFormat;
import org.onexus.website.api.widgets.download.formats.IDownloadFormat;
import org.onexus.website.api.widgets.download.formats.TsvFormat;
import org.onexus.website.api.widgets.download.scripts.*;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.IOException;
import java.io.OutputStream;
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

	private final static List<IDownloadFormat> formats = Arrays.asList( new IDownloadFormat[] {
		    new TsvFormat(),
			new ExcelFormat()
	});

    private String webserviceUrl;
    private IDownloadFormat format;

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
		setFormat(formats.get(0));
		downloadForm.add(new DropDownChoice<IDownloadFormat>("format", new PropertyModel<IDownloadFormat>(this, "format"), formats));
		add(downloadForm);

		final AjaxDownloadBehavior ajaxDownloadBehavior = new AjaxDownloadBehavior() {
			@Override
			protected String getFileName() {
				return getFormat().getFileName();
			}

			@Override
			protected IResource getResource() {
				return new DownloadResource();
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

    public IDownloadFormat getFormat() {
        return format;
    }

    public void setFormat(IDownloadFormat format) {
        this.format = format;
    }

    private ICollectionManager getCollectionManager() {

        if (collectionManager == null) {
            WebsiteApplication.inject(this);
        }

        return collectionManager;
    }

    private class DownloadResource extends AbstractResource {

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {

            ResourceResponse resourceResponse = new ResourceResponse();

            resourceResponse.setContentDisposition(ContentDisposition.ATTACHMENT);
            resourceResponse.setContentType(getFormat().getContentType());
            resourceResponse.setFileName(getFormat().getFileName());
            resourceResponse.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(Attributes attributes) throws IOException {

                    Query query = getQuery();
                    IDownloadFormat format = getFormat();

                    if (format.getMaxRowsLimit() != null) {
                        if (query.getCount() == null || format.getMaxRowsLimit() < query.getCount()) {
                            query.setCount(format.getMaxRowsLimit());
                        }
                    }

                    IEntityTable table = getCollectionManager().load(query);
                    OutputStream out = attributes.getResponse().getOutputStream();
                    getFormat().write(table, out);
                }
            });

            return resourceResponse;
        }
    }


}
