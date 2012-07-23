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

import org.apache.wicket.model.IModel;
import org.onexus.ui.api.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

public class TableViewerCreator extends AbstractWidgetCreator<TableViewerConfig, TableViewerStatus> {

    public TableViewerCreator() {
        super(TableViewerConfig.class, "table-viewer", "Collection table viewer");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<TableViewerStatus> statusModel) {
        return new TableViewer(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(TableViewerConfig.class);
        resourceRegister.register(ColumnConfig.class);
        resourceRegister.register(ColumnSet.class);
    }

}
