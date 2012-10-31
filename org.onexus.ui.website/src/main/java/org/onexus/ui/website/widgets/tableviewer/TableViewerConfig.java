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
package org.onexus.ui.website.widgets.tableviewer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.resource.api.ORI;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("viewer-table")
public class TableViewerConfig extends WidgetConfig {
    
    private ORI collection;

    private TableViewerStatus defaultStatus;

    @XStreamImplicit(itemFieldName="columnset")
    private List<ColumnSet> columnSets = new ArrayList<ColumnSet>();

    public TableViewerConfig() {
        super();
    }

    public TableViewerConfig(String id, ORI collection) {
        super(id);
        this.collection = collection;
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public List<ColumnSet> getColumnSets() {
        return columnSets;
    }

    public void setColumnSets(List<ColumnSet> columnSets) {
        this.columnSets = columnSets;
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
