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

import org.apache.wicket.markup.html.link.InlineFrame;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.IQueryContributor;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

import javax.inject.Inject;

public class HeatmapViewer extends Widget<HeatmapViewerConfig, HeatmapViewerStatus> implements IQueryContributor {
    
    @Inject
    public IResourceManager resourceManager;    

    public HeatmapViewer(String componentId, IWidgetModel status) {
        super(componentId, status);

        onEventFireUpdate(EventQueryUpdate.class, EventFixEntity.class, EventUnfixEntity.class);
    }

    @Override
    protected void onBeforeRender() {

        addOrReplace(new InlineFrame("heatmap", new HeatmapPage(getConfig(), getQuery())));

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

    @Override
    public void onQueryBuild(Query query) {

        String releaseURI = getReleaseURI();
        String collectionURI = getConfig().getCollection();

        query.setOn( releaseURI );
        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionURI);
        query.setFrom(collectionAlias);

        for (ColumnConfig column : getConfig().getColumns()) {
            column.buildQuery(query);
        }

        for (ColumnConfig column : getConfig().getRows()) {
            column.buildQuery(query);
        }

        for (ColumnConfig column : getConfig().getCells()) {
            column.buildQuery(query);
        }
    }
}
