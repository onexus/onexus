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
