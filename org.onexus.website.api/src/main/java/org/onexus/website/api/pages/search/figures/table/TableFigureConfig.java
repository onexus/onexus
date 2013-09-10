package org.onexus.website.api.pages.search.figures.table;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.search.FigureConfig;
import org.onexus.website.api.pages.search.figures.bar.CollectionField;
import org.onexus.website.api.widgets.tableviewer.columns.IColumnConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("figure-table")
public class TableFigureConfig extends FigureConfig {

    private ORI collection;

    private OrderBy order;

    private Integer limit;

    private List<IColumnConfig> columns = new ArrayList<IColumnConfig>();

    private String define;

    private String where;

    private String empty;

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public OrderBy getOrder() {
        return order;
    }

    public void setOrder(OrderBy order) {
        this.order = order;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<IColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<IColumnConfig> columns) {
        this.columns = columns;
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }
}
