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

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.Query;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;
import org.onexus.ui.workspace.progressbar.ProgressBar;
import org.onexus.ui.workspace.progressbar.ProgressBar.ActiveTasks;

import java.util.ArrayList;
import java.util.List;

public class TableViewer extends Widget<TableViewerConfig, TableViewerStatus> {

    public static final CssResourceReference TABLE_VIEWER_CSS = new CssResourceReference(TableViewer.class,
            "TableViewer.css");

    private final static MetaDataKey<Integer> DEFAULT_ROWS_PER_PAGE = new MetaDataKey<Integer>() {
    };

    // Model objects
    private EntitiesRowProvider dataProvider;

    public TableViewer(String componentId, IModel<TableViewerStatus> status) {
        super(componentId, status);

        onEventFireUpdate(EventQueryUpdate.class, EventFixEntity.class, EventUnfixEntity.class);

        if (status.getObject() == null) {
            status.setObject(getStatus());
        }

        TableViewerConfig config = getConfig();

        this.getVariation();

        this.dataProvider = new EntitiesRowProvider(config, status) {

            @Override
            protected Query getQuery() {
                return TableViewer.this.getQuery();
            }

            @Override
            protected void addTaskStatus(TaskStatus taskStatus) {

                ActiveTasks activeTasks = Session.get().getMetaData(ProgressBar.TASKS);
                if (activeTasks == null) {
                    activeTasks = new ActiveTasks();
                    Session.get().setMetaData(ProgressBar.TASKS, activeTasks);
                }
                for (TaskStatus task : taskStatus.getSubTasks()) {
                    activeTasks.addTask(task);
                }
            }

        };
        Integer rowsPerPage = getSession().getMetaData(DEFAULT_ROWS_PER_PAGE);

        // Create the columns from the config
        int ccs = getStatus().getCurrentColumnSet();
        List<IColumnConfig> columnsConfig = getConfig().getColumnSets().get(ccs).getColumns();
        List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();

        String parentURI = getQuery().getOn();

        for (IColumnConfig columnConfig : columnsConfig) {
            columnConfig.addColumns(columns, parentURI );
        }

        DataTable<IEntityTable, String> resultTable = new DataTable<IEntityTable, String>("datatable", columns, dataProvider,
                (rowsPerPage == null ? 45 : rowsPerPage));
        resultTable.setOutputMarkupId(true);
        resultTable.setVersioned(false);
        resultTable.addTopToolbar(new HeadersToolbar(resultTable, dataProvider));
        resultTable.addBottomToolbar(new NoRecordsToolbar(resultTable));
        resultTable.addBottomToolbar(new AjaxNavigationToolbar(resultTable));
        add(resultTable);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(TABLE_VIEWER_CSS));
    }

}
