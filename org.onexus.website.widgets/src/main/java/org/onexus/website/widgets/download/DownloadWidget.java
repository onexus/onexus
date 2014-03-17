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
package org.onexus.website.widgets.download;

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
import org.onexus.website.widgets.download.formats.ExcelFormat;
import org.onexus.website.widgets.download.formats.IDownloadFormat;
import org.onexus.website.widgets.download.formats.TsvFormat;
import org.onexus.website.widgets.download.scripts.*;
import org.onexus.website.widgets.browser.BrowserPageStatus;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DownloadWidget extends Widget<DownloadWidgetConfig, DownloadWidgetStatus> {

    public static final ResourceReference CSS = new CssResourceReference(DownloadWidget.class, "prettify/prettify.css");
    public static final ResourceReference JS = new JavaScriptResourceReference(DownloadWidget.class, "prettify/prettify.js");
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    @Inject
    private ICollectionManager collectionManager;

    public static final Map<String, IQueryScript> SCRIPTS_MAP = new LinkedHashMap<String, IQueryScript>();

    static {
        addScript(new RScript());
        addScript(new Python2Script());
        addScript(new Python3Script());
        addScript(new PerlScript());
        addScript(new BashScript());
    }

    private static void addScript(IQueryScript queryScript) {
        SCRIPTS_MAP.put(queryScript.getLabel().toLowerCase(), queryScript);
    }

    private static final Map<String, IDownloadFormat> FORMATS_MAP = new LinkedHashMap<String, IDownloadFormat>();

    static {
        addFormat(new TsvFormat());
        addFormat(new ExcelFormat());
    }

    private static void addFormat(IDownloadFormat format) {
        FORMATS_MAP.put(format.getLabel().toLowerCase(), format);
    }


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

        List<IDownloadFormat> formats = getFormats();
        if (formats.isEmpty()) {
            throw new UnsupportedOperationException("Download widget: At least one format is needed.");
        }
        setFormat(formats.get(0));
        downloadForm.add(new DropDownChoice<IDownloadFormat>("format", new PropertyModel<IDownloadFormat>(this, "format"), formats));
        add(downloadForm);

        final AjaxDownloadBehavior ajaxDownloadBehavior = new AjaxDownloadBehavior() {
            @Override
            protected String getFileName() {
                return DownloadWidget.this.getFileName();
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

        List<IQueryScript> scripts = getScripts();
        // Add scripts
        ListView scriptsView = new ListView<IQueryScript>("scripts", scripts) {
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
        };

        if (scripts.isEmpty()) {
            scriptsView.setVisible(false);
        }
        add(scriptsView);

    }

    private String getFileName() {
        BrowserPageStatus status = DownloadWidget.this.findParentStatus(BrowserPageStatus.class);

        if (status == null) {
            return getFormat().getFileName("datafile");
        }

        return getFormat().getFileName(status.getCurrentTabId());
    }

    private List<IDownloadFormat> getFormats() {
        List<IDownloadFormat> formats = new ArrayList<IDownloadFormat>();

        String formatsStr = getConfig().getFormats();

        if (formatsStr == null) {
            formats.addAll(FORMATS_MAP.values());
        } else {
            String[] values = COMMA_PATTERN.split(formatsStr);
            for (String value : values) {
                IDownloadFormat format = FORMATS_MAP.get(value.trim().toLowerCase());
                if (format != null) {
                    formats.add(format);
                }
            }
        }

        return formats;
    }


    private List<IQueryScript> getScripts() {
        List<IQueryScript> scripts = new ArrayList<IQueryScript>();

        String scriptsStr = getConfig().getScripts();

        if (scriptsStr == null) {
            scripts.addAll(SCRIPTS_MAP.values());
        } else {
            String[] values = COMMA_PATTERN.split(scriptsStr);
            for (String value : values) {
                IQueryScript script = SCRIPTS_MAP.get(value.trim().toLowerCase());
                if (script != null) {
                    scripts.add(script);
                }
            }
        }

        return scripts;
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
            resourceResponse.setFileName(getFileName());
            resourceResponse.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(Attributes attributes) throws IOException {

                    Query query = getQuery();
                    IDownloadFormat format = getFormat();

                    if (format.getMaxRowsLimit() != null && (query.getCount() == null || format.getMaxRowsLimit() < query.getCount())) {
                        query.setCount(format.getMaxRowsLimit());
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
