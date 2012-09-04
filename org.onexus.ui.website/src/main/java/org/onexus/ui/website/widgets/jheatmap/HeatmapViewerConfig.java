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
package org.onexus.ui.website.widgets.jheatmap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("viewer-heatmap")
public class HeatmapViewerConfig extends WidgetConfig {

    private HeatmapViewerStatus defaultStatus;
    
    private String collection;
   
    private String init;

    @XStreamImplicit(itemFieldName="column")
    private List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

    @XStreamImplicit(itemFieldName="row")
    private List<ColumnConfig> rows = new ArrayList<ColumnConfig>();

    @XStreamImplicit(itemFieldName="cell")
    private List<ColumnConfig> cells = new ArrayList<ColumnConfig>();


    public HeatmapViewerConfig() {
        super();
    }

    public HeatmapViewerConfig(String id) {
        super(id);
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public List<ColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfig> columns) {
        this.columns = columns;
    }

    public List<ColumnConfig> getRows() {
        return rows;
    }

    public void setRows(List<ColumnConfig> rows) {
        this.rows = rows;
    }

    public List<ColumnConfig> getCells() {
        return cells;
    }

    public void setCells(List<ColumnConfig> cells) {
        this.cells = cells;
    }

    @Override
    public HeatmapViewerStatus createEmptyStatus() {
        return new HeatmapViewerStatus(getId());
    }

    public HeatmapViewerStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(HeatmapViewerStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}