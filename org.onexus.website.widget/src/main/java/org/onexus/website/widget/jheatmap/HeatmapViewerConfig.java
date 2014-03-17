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

import org.onexus.resource.api.ORI;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.website.api.widget.WidgetConfig;
import org.onexus.website.widget.tableviewer.columns.ColumnConfig;

import java.util.ArrayList;
import java.util.List;

@ResourceAlias("viewer-heatmap")
public class HeatmapViewerConfig extends WidgetConfig {

    private HeatmapViewerStatus defaultStatus;

    private ORI collection;

    private String init;

    private String colAnnotations;

    private String rowAnnotations;

    @ResourceImplicitList("column")
    private List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

    @ResourceImplicitList("row")
    private List<ColumnConfig> rows = new ArrayList<ColumnConfig>();

    @ResourceImplicitList("cell")
    private List<ColumnConfig> cells = new ArrayList<ColumnConfig>();


    public HeatmapViewerConfig() {
        super();
    }

    public HeatmapViewerConfig(String id) {
        super(id);
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
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

    public String getColAnnotations() {
        return colAnnotations;
    }

    public void setColAnnotations(String colAnnotations) {
        this.colAnnotations = colAnnotations;
    }

    public String getRowAnnotations() {
        return rowAnnotations;
    }

    public void setRowAnnotations(String rowAnnotations) {
        this.rowAnnotations = rowAnnotations;
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
