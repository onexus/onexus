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
package org.onexus.ui.website.widgets.heatmap;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Order;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.export.ExportResource;
import org.onexus.ui.website.widgets.tableviewer.TableViewerStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeatmapViewer extends Widget<HeatmapViewerConfig, HeatmapViewerStatus> {
    
    @Inject
    public IResourceManager resourceManager;    

    public HeatmapViewer(String componentId, IWidgetModel status) {
        super(componentId, status);

        onEventFireUpdate(EventQueryUpdate.class, EventFixEntity.class, EventUnfixEntity.class);
    }

    @Override
    protected void onBeforeRender() {

        String releaseURI = getReleaseURI();
        String collectionURI = getConfig().getCollection();
        
        Query query = new Query(collectionURI);
        query.setMainNamespace( releaseURI );

        for (ColumnConfig column : getConfig().getColumns()) {
            query.getCollections().add(column.getCollection());
        }

        for (ColumnConfig column : getConfig().getRows()) {
            query.getCollections().add(column.getCollection());
        }

        for (ColumnConfig column : getConfig().getCells()) {
            query.getCollections().add(column.getCollection());
        }

        buildQuery(query);

        addOrReplace(new InlineFrame("heatmap", new HeatmapPage(getConfig(), query)));

        super.onBeforeRender();
        
    }
    

    
    private String getReleaseURI() {

        BrowserPageStatus browserStatus = getPageStatus();
        return (browserStatus != null ? browserStatus.getReleaseURI() : null);
    }

    private BrowserPageStatus getPageStatus() {
        IPageModel pageModel = getPageModel();

        return (BrowserPageStatus) (pageModel == null ? null : pageModel.getObject());
    };

    private BrowserPageConfig getPageConfig() {
        IPageModel pageModel = getPageModel();

        return (BrowserPageConfig) (pageModel == null ? null : pageModel.getConfig());
    };

}
