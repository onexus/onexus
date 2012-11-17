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
