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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Progress;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.utils.panels.ondomready.OnDomReadyPanel;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.tableviewer.columns.IColumnConfig;

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

        onEventFireUpdate(EventQueryUpdate.class, EventAddFilter.class, EventRemoveFilter.class);

        if (status.getObject() == null) {
            status.setObject(getStatus());
        }

        this.getVariation();

        Integer sessionRowsPerPage = getSession().getMetaData(DEFAULT_ROWS_PER_PAGE);
        final Integer rowsPerPage = (sessionRowsPerPage == null ? 30 : sessionRowsPerPage);

        this.dataProvider = new EntitiesRowProvider(status, rowsPerPage) {

            @Override
            protected Query getQuery() {
                return TableViewer.this.getQuery();
            }

            @Override
            protected void addTaskStatus(Progress progressStatus) {

                /*TODO
                ActiveProgress activeProgress = Session.get().getMetaData(ProgressBar.TASKS);
                if (activeProgress == null) {
                    activeProgress = new ActiveProgress();
                    Session.get().setMetaData(ProgressBar.TASKS, activeProgress);
                }
                for (Progress progress : progressStatus.getSubProgresses()) {
                    activeProgress.addTask(progress);
                } */
            }

        };

        // Create the columns from the config
        int ccs = getStatus().getCurrentColumnSet();
        List<IColumnConfig> columnsConfig = getConfig().getColumnSets().get(ccs).getColumns();
        final List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();

        ORI parentURI = getQuery().getOn();

        List<IColumnConfig> visibleColumnsConfig = new ArrayList<IColumnConfig>(columnsConfig.size());
        BrowserPageStatus pageStatus = findParentStatus(BrowserPageStatus.class);

        if (pageStatus != null) {
            Predicate filter = new VisiblePredicate(getPageBaseOri(), pageStatus.getFilters());
            CollectionUtils.select(columnsConfig, filter, visibleColumnsConfig);
        } else {
            visibleColumnsConfig = columnsConfig;
        }

        for (IColumnConfig columnConfig : visibleColumnsConfig) {
            columnConfig.addColumns(columns, parentURI);
        }

        add(new OnDomReadyPanel("datatable") {
            @Override
            protected Panel onDomReadyPanel(String componentId) {
                return new DataTablePanel("datatable", columns, dataProvider, rowsPerPage);
            }
        });

    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);

        if (event.getPayload() instanceof EventQueryUpdate) {
            this.dataProvider.clearCount();
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(TABLE_VIEWER_CSS));
    }

}
