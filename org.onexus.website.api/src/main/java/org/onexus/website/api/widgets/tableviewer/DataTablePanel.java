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
package org.onexus.website.api.widgets.tableviewer;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.collection.api.IEntityTable;
import org.onexus.website.api.utils.panels.ondomready.OnDomReadyPanel;

import java.util.List;

public class DataTablePanel extends Panel implements IAjaxIndicatorAware {

    private WebMarkupContainer indicator;

    private transient EntitiesRowProvider dataProvider;

    public DataTablePanel(String id, final List<? extends IColumn<IEntityTable, String>> columns,
                          final EntitiesRowProvider dataProvider, final long rowsPerPage) {
        super(id);

        this.dataProvider = dataProvider;

        indicator = new WebMarkupContainer("indicator");
        indicator.setOutputMarkupId(true);
        indicator.add(new Image("loading", OnDomReadyPanel.LOADING_IMAGE));
        add(indicator);

        DataTable<IEntityTable, String> resultTable = new DataTable<IEntityTable, String>("datatable", columns, dataProvider, rowsPerPage);
        resultTable.setOutputMarkupId(true);
        resultTable.setVersioned(false);
        resultTable.addTopToolbar(new HeadersToolbar(resultTable, dataProvider));
        resultTable.addBottomToolbar(new NoRecordsToolbar(resultTable));
        resultTable.addBottomToolbar(new NavigationToolbar(resultTable));
        add(resultTable);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicator.getMarkupId();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        if (dataProvider!=null) {
            dataProvider.close();
        }
    }
}
