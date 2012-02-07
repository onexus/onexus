/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.viewers.tableviewer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.ui.website.viewers.ViewerConfig;
import org.onexus.ui.website.viewers.tableviewer.columns.IColumnConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("viewer-table")
public class TableViewerConfig extends ViewerConfig {

    private String mainCollection;

    private TableViewerStatus defaultStatus;

    private List<IColumnConfig> columns = new ArrayList<IColumnConfig>();

    public TableViewerConfig() {
        super();
    }

    public TableViewerConfig(String id, String mainCollectionURI) {
        super(id);
        this.mainCollection = mainCollectionURI;
    }

    public String getMainCollection() {
        return mainCollection;
    }

    public void setMainCollection(String mainCollectionURI) {
        this.mainCollection = mainCollectionURI;
    }

    public List<IColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<IColumnConfig> columns) {
        this.columns = columns;
    }

    public void addColumn(IColumnConfig column) {
        this.columns.add(column);
    }

    @Override
    public TableViewerStatus createEmptyStatus() {
        return new TableViewerStatus(getId());
    }

    public TableViewerStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(TableViewerStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
