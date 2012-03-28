package org.onexus.ui.website.widgets.tableviewer;


import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColumnSet implements Serializable {
    
    private String title;

    private List<IColumnConfig> columns = new ArrayList<IColumnConfig>();

    public ColumnSet() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<IColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<IColumnConfig> columns) {
        this.columns = columns;
    }
    
    public String toString() {
        return title;
    }
}
