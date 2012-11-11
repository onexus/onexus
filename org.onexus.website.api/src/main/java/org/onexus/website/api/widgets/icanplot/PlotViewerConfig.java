package org.onexus.website.api.widgets.icanplot;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetStatus;
import org.onexus.website.api.widgets.tableviewer.columns.IColumnConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("viewer-plot")
public class PlotViewerConfig extends WidgetConfig {

    private PlotViewerStatus defaultStatus;

    private ORI collection;

    private List<IColumnConfig> columns = new ArrayList<IColumnConfig>();

    private PlotFields fields;

    @Override
    public WidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    @Override
    public WidgetStatus createEmptyStatus() {
        return new PlotViewerStatus(getId(), fields);
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public List<IColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<IColumnConfig> columns) {
        this.columns = columns;
    }

    public PlotFields getFields() {
        return fields;
    }

    public void setFields(PlotFields fields) {
        this.fields = fields;
    }
}
