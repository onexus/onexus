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
package org.onexus.website.widget.tableviewer;

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
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Progress;
import org.onexus.website.api.events.EventAddFilter;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.events.EventRemoveFilter;
import org.onexus.website.api.utils.panels.ondomready.OnDomReadyPanel;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widget.Widget;
import org.onexus.website.widget.browser.BrowserPageStatus;
import org.onexus.website.widget.tableviewer.columns.IColumnConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableViewer extends Widget<TableViewerConfig, TableViewerStatus> {

    public static final CssResourceReference TABLE_VIEWER_CSS = new CssResourceReference(TableViewer.class,
            "TableViewer.css");

    public static final MetaDataKey<Integer> DEFAULT_ROWS_PER_PAGE = new MetaDataKey<Integer>() {
    };

    // Model objects
    private EntitiesRowProvider dataProvider;

    public TableViewer(String componentId, IModel<TableViewerStatus> status) {
        super(componentId, status);

        onEventFireUpdate(EventQueryUpdate.class, EventAddFilter.class, EventRemoveFilter.class);

        if (status.getObject() == null) {
            status.setObject(getStatus());
        }

        Integer sessionRowsPerPage = getSession().getMetaData(DEFAULT_ROWS_PER_PAGE);
        final Integer rowsPerPage = sessionRowsPerPage == null ? 20 : sessionRowsPerPage;

        this.dataProvider = new EntitiesRowProvider(status, rowsPerPage) {

            @Override
            protected Query getQuery() {
                return TableViewer.this.getQuery();
            }

            @Override
            protected void addTaskStatus(Progress progressStatus) {
                //TODO
            }

        };

        // Create the columns from the config
        int ccs = getStatus().getCurrentColumnSet();
        List<IColumnConfig> columnsConfig = getConfig().getColumnSets().get(ccs).getColumns();
        final List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();

        ORI parentURI = getQuery().getOn();

        List<IColumnConfig> visibleColumnsConfig = new ArrayList<IColumnConfig>(columnsConfig.size());
        BrowserPageStatus pageStatus = findParentStatus(BrowserPageStatus.class);

        Predicate sortablePredicate;
        if (pageStatus != null) {
            sortablePredicate = new VisiblePredicate(getConfig().getORI(), pageStatus.getEntitySelections());
            Predicate filter = new VisiblePredicate(getConfig().getORI(), pageStatus.getEntitySelections());
            CollectionUtils.select(columnsConfig, filter, visibleColumnsConfig);
        } else {
            sortablePredicate = new VisiblePredicate(getConfig().getORI(), Collections.EMPTY_LIST);
            visibleColumnsConfig = columnsConfig;
        }

        boolean tableSort = sortablePredicate.evaluate(getConfig().getSortable());
        for (IColumnConfig columnConfig : visibleColumnsConfig) {
            String columnSortStr = columnConfig.getSortable();
            boolean columnSort = sortablePredicate.evaluate(columnSortStr);
            columnConfig.addColumns(columns, parentURI, Strings.isEmpty(columnSortStr) ? tableSort : columnSort);
        }

        // Disable default status order if the table is not sortable.
        if (!tableSort) {
            getStatus().setOrder(null);
        }

        add(new OnDomReadyPanel("datatable") {
            @Override
            protected Panel onDomReadyPanel(String componentId) {

                Boolean forceCount = getConfig().getForceCount();
                if (forceCount != null && forceCount.booleanValue()) {
                    dataProvider.forceCount();
                }

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
