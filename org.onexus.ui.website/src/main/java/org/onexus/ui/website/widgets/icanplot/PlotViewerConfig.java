package org.onexus.ui.website.widgets.icanplot;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("viewer-plot")
public class PlotViewerConfig extends WidgetConfig {

    private PlotViewerStatus defaultStatus;

    private String collection;

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

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
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
