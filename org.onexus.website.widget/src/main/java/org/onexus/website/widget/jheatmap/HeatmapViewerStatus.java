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
package org.onexus.website.widget.jheatmap;

import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widget.WidgetStatus;
import org.onexus.website.widget.tableviewer.columns.ColumnConfig;

public class HeatmapViewerStatus extends WidgetStatus<HeatmapViewerConfig> {

    public HeatmapViewerStatus() {
        super();
    }

    public HeatmapViewerStatus(String viewerId) {
        super(viewerId);
    }

    @Override
    public void onQueryBuild(Query query) {

        ORI collectionURI = getConfig().getCollection();

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
