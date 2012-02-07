/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
import org.onexus.core.query.Order;
import org.onexus.core.query.Query;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.tabs.Tab;
import org.onexus.ui.website.viewers.ViewerConfig;
import org.onexus.ui.website.viewers.tableviewer.TableViewerConfig;
import org.onexus.ui.website.viewers.tableviewer.TableViewerStatus;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig.ExportColumn;
import org.onexus.ui.website.viewers.tableviewer.columns.IColumnConfig;
import org.onexus.ui.website.widgets.Widget;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExportWidget extends Widget<ExportWidgetConfig, ExportWidgetStatus> {

    public ExportWidget(String componentId, ExportWidgetConfig config, IModel<ExportWidgetStatus> statusModel) {
        super(componentId, config, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);
    }

    @Override
    protected void onBeforeRender() {

        Tab<?, ?> tab = findParent(Tab.class);
        ViewerConfig config = tab.getCurrentViewer();

        if (config instanceof TableViewerConfig) {

            TableViewerConfig tc = (TableViewerConfig) config;

            //FIXME
            BrowserPage browserPage = findParent(BrowserPage.class);
            String releaseURI = (browserPage != null ? browserPage.getStatus().getReleaseURI() : null);
            Query query = new Query(tc.getMainCollection());
            query.setMainNamespace(releaseURI);

            List<ExportColumn> exportColumns = new ArrayList<ExportColumn>();
            for (IColumnConfig column : tc.getColumns()) {
                column.addExportColumns(exportColumns, releaseURI);
                for (String collectionId : column.getQueryCollections(releaseURI)) {
                    query.getCollections().add(ResourceTools.getAbsoluteURI(releaseURI, collectionId));
                }
            }

            buildQuery(query);

            TableViewerStatus vs = getTableViewerStatus();

            if (vs != null) {
                Order order = vs.getOrder();
                if (order != null) {
                    query.setOrder(order);
                }
            }

            String statusEncoded;
            try {
                statusEncoded = ExportResource.encodeQuery(query);
            } catch (UnsupportedEncodingException e) {
                throw new WicketRuntimeException("Unable to encode the URL parameter 'query'", e);
            }

            StringBuilder columnsString = new StringBuilder();
            Iterator<ExportColumn> it = exportColumns.iterator();

            while (it.hasNext()) {
                columnsString.append(it.next().toString());
                if (it.hasNext()) {
                    columnsString.append(":::");
                }
            }

            PageParameters params = new PageParameters();
            params.add(ExportResource.STATUS, statusEncoded);
            params.add(ExportResource.FILENAME, "file-name." + ExportResource.FORMAT_TSV);
            params.add(ExportResource.COLUMNS, columnsString);

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

    private TableViewerStatus getTableViewerStatus() {
        IModel<TableViewerStatus> statusModel = findInnerModel(TableViewerStatus.class, getDefaultModel());
        return (statusModel == null ? null : statusModel.getObject());

    }

}
