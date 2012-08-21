package org.onexus.ui.website.widgets.icanplot;

import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

public class PlotViewerStatus extends WidgetStatus<PlotViewerConfig> {

    public PlotViewerStatus() {
    }

    public PlotViewerStatus(String widgetId) {
        super(widgetId);
    }

    @Override
    public void onQueryBuild(Query query) {

        String collectionURI = getConfig().getCollection();

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionURI);
        query.setFrom(collectionAlias);

        for (IColumnConfig column : getConfig().getColumns()) {
            column.buildQuery(query);
        }

    }
}
