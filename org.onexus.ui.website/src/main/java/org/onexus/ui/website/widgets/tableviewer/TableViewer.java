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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.data.api.Progress;
import org.onexus.ui.website.events.EventAddFilter;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventRemoveFilter;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.utils.visible.VisiblePredicate;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;
import org.onexus.ui.api.progressbar.ProgressBar;
import org.onexus.ui.api.progressbar.ProgressBar.ActiveProgress;

import java.util.ArrayList;
import java.util.List;

public class TableViewer extends Widget<TableViewerConfig, TableViewerStatus> implements IAjaxIndicatorAware {

    public static final CssResourceReference TABLE_VIEWER_CSS = new CssResourceReference(TableViewer.class,
            "TableViewer.css");

    private final static MetaDataKey<Integer> DEFAULT_ROWS_PER_PAGE = new MetaDataKey<Integer>() {
    };

    // Model objects
    private EntitiesRowProvider dataProvider;

    private final WebMarkupContainer indicatorAppender;

    public TableViewer(String componentId, IModel<TableViewerStatus> status) {
        super(componentId, status);

        indicatorAppender = new WebMarkupContainer("progress");
        indicatorAppender.setOutputMarkupId(true);
        indicatorAppender.add(new Image("image", AbstractDefaultAjaxBehavior.INDICATOR));
        add(indicatorAppender);

        onEventFireUpdate(EventQueryUpdate.class, EventAddFilter.class, EventRemoveFilter.class);

        if (status.getObject() == null) {
            status.setObject(getStatus());
        }

        TableViewerConfig config = getConfig();

        this.getVariation();

        Integer rowsPerPage = getSession().getMetaData(DEFAULT_ROWS_PER_PAGE);
        rowsPerPage = (rowsPerPage == null ? 30 : rowsPerPage);

        this.dataProvider = new EntitiesRowProvider(config, status, rowsPerPage) {

            @Override
            protected Query getQuery() {
                return TableViewer.this.getQuery();
            }

            @Override
            protected void addTaskStatus(Progress progressStatus) {

                ActiveProgress activeProgress = Session.get().getMetaData(ProgressBar.TASKS);
                if (activeProgress == null) {
                    activeProgress = new ActiveProgress();
                    Session.get().setMetaData(ProgressBar.TASKS, activeProgress);
                }
                for (Progress progress : progressStatus.getSubProgresses()) {
                    activeProgress.addTask(progress);
                }
            }

        };

        // Create the columns from the config
        int ccs = getStatus().getCurrentColumnSet();
        List<IColumnConfig> columnsConfig = getConfig().getColumnSets().get(ccs).getColumns();
        List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();

        String parentURI = getQuery().getOn();

        List<IColumnConfig> visibleColumnsConfig = new ArrayList<IColumnConfig>(columnsConfig.size());
        BrowserPageStatus pageStatus = findParentStatus(status, BrowserPageStatus.class);

        if (pageStatus!=null) {
            Predicate filter = new VisiblePredicate(pageStatus.getBase(), pageStatus.getFilters());
            CollectionUtils.select(columnsConfig, filter, visibleColumnsConfig);
        } else {
            visibleColumnsConfig = columnsConfig;
        }

        for (IColumnConfig columnConfig : visibleColumnsConfig) {
            columnConfig.addColumns(columns, parentURI );
        }

        DataTable<IEntityTable, String> resultTable = new DataTable<IEntityTable, String>("datatable", columns, dataProvider, rowsPerPage);
        resultTable.setOutputMarkupId(true);
        resultTable.setVersioned(false);
        resultTable.addTopToolbar(new HeadersToolbar(resultTable, dataProvider));
        resultTable.addBottomToolbar(new NoRecordsToolbar(resultTable));
        resultTable.addBottomToolbar(new NavigationToolbar(resultTable));
        add(resultTable);

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

    /**
     * @see org.apache.wicket.ajax.IAjaxIndicatorAware#getAjaxIndicatorMarkupId()
     */
    public String getAjaxIndicatorMarkupId()
    {
        return indicatorAppender.getMarkupId();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        dataProvider.close();
    }
}
