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
package org.onexus.ui.website.widgets.export;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.query.Query;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.TableViewerStatus;

import java.io.UnsupportedEncodingException;

public class ExportWidget extends Widget<ExportWidgetConfig, ExportWidgetStatus> {

    public ExportWidget(String componentId, IModel<ExportWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);
    }

    @Override
    protected void onBeforeRender() {


        TableViewerConfig config = getTableViewerConfig();
        TableViewerStatus vs = getTableViewerStatus();
        if (config != null && vs != null) {

            Query query = getQuery();

            String statusEncoded;
            try {
                statusEncoded = ExportResource.encodeQuery(query);
            } catch (UnsupportedEncodingException e) {
                throw new WicketRuntimeException("Unable to encode the URL parameter 'query'", e);
            }

            PageParameters params = new PageParameters();
            params.add(ExportResource.STATUS, statusEncoded);
            params.add(ExportResource.FILENAME, "file-name." + ExportResource.FORMAT_TSV);

            ResourceReference rr = Application.get().getSharedResources()
                    .get(Application.class, "export", null, null, null, true);
            addOrReplace(new ResourceLink<String>("tsvLink", rr, params));

        } else {
            addOrReplace(new AjaxLink<String>("tsvLink") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    target.add(ExportWidget.this);
                }

            }.setEnabled(false));
        }

        super.onBeforeRender();

    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    };

    private BrowserPageConfig getPageConfig() {
        return (BrowserPageConfig) getPageStatus().getConfig();
    };

    private TableViewerStatus getTableViewerStatus() {

        TableViewerConfig tableConfig = getTableViewerConfig();
        if (tableConfig == null) {
            return null;
        }

        String widgetId = getTableViewerConfig().getId();
        if (widgetId == null) {
            return null;
        }

        return (TableViewerStatus) getPageStatus().getWidgetStatus(widgetId);

    }

    private TableViewerConfig getTableViewerConfig() {

        try {
            String currentTab = getPageStatus().getCurrentTabId();
            String currentView = getPageStatus().getCurrentView();
            String mainWidgetId = getPageConfig().getTab(currentTab).getView(currentView).getMain().trim();
            WidgetConfig widgetConfig = getPageConfig().getWidget(mainWidgetId);

            if (widgetConfig instanceof TableViewerConfig) {
                return (TableViewerConfig) widgetConfig;
            }
        } catch (NullPointerException e) {
            // Return null on any null pointer exception
        }

        return null;
    }


}
