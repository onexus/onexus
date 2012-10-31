package org.onexus.ui.website.widgets.icanplot;

import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

public class PlotViewerStatus extends WidgetStatus<PlotViewerConfig> {

    private PlotFields fields;

    public PlotViewerStatus() {
    }

    public PlotViewerStatus(String widgetId) {
        super(widgetId);
    }

    public PlotViewerStatus(String widgetId, PlotFields fields) {
        super(widgetId);
        this.fields = fields;
    }

    public PlotFields getFields() {
        return fields;
    }

    public void setFields(PlotFields fields) {
        this.fields = fields;
    }

    @Override
    public void onQueryBuild(Query query) {

        ORI collectionURI = getConfig().getCollection();

        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionURI);
        query.setFrom(collectionAlias);

        for (IColumnConfig column : getConfig().getColumns()) {
            column.buildQuery(query);
        }

    }
}
