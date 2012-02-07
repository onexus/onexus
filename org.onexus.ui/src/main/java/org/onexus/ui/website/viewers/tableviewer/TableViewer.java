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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.Query;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.viewers.Viewer;
import org.onexus.ui.website.viewers.tableviewer.columns.IColumnConfig;
import org.onexus.ui.workspace.progressbar.TaskStatusProgress;
import org.onexus.ui.workspace.progressbar.TaskStatusProgress.ActiveTasks;

public class TableViewer extends Viewer<TableViewerConfig, TableViewerStatus> {

    public static final CssResourceReference TABLE_VIEWER_CSS = new CssResourceReference(TableViewer.class,
	    "TableViewer.css");

    private final static MetaDataKey<Integer> DEFAULT_ROWS_PER_PAGE = new MetaDataKey<Integer>() {
    };

    // Model objects
    private EntitiesRowProvider dataProvider;

    public TableViewer(String componentId, TableViewerConfig config, IModel<TableViewerStatus> status) {
	super(componentId, config, status);

	onEventFireUpdate(EventQueryUpdate.class, EventFixEntity.class, EventUnfixEntity.class);

	if (status.getObject() == null) {
	    status.setObject(getStatus());
	}

	this.dataProvider = new EntitiesRowProvider(config, status) {

	    @Override
	    protected void buildQuery(Query query) {
		TableViewer.this.buildQuery(query);
	    }

	    @Override
	    protected void addTaskStatus(TaskStatus taskStatus) {

		ActiveTasks activeTasks = Session.get().getMetaData(TaskStatusProgress.TASKS);
		if (activeTasks == null) {
		    activeTasks = new ActiveTasks();
		    Session.get().setMetaData(TaskStatusProgress.TASKS, activeTasks);
		}
		for (TaskStatus task : taskStatus.getSubTasks()) {
		    activeTasks.addTask(task);
		}
	    }
	    
	    @Override
	    protected BrowserPageStatus getBrowserPageStatus() {
		//FIXME
		WebsiteStatus websiteStatus = TableViewer.this.getWebsiteStatus();
		return (websiteStatus == null ? null : (BrowserPageStatus) websiteStatus.getCurrentPageStatus());
	    }

	};
	Integer rowsPerPage = getSession().getMetaData(DEFAULT_ROWS_PER_PAGE);

	WebsiteStatus websiteStatus = getWebsiteStatus();
	BrowserPageStatus browserPageStatus = (websiteStatus == null? null : ((BrowserPageStatus) websiteStatus.getCurrentPageStatus()));
	String releaseURI = (browserPageStatus == null) ? null : browserPageStatus.getReleaseURI();

	// Create the columns from the config
	List<IColumnConfig> columnsConfig = getConfig().getColumns();
	List<IColumn<IEntityTable>> columns = new ArrayList<IColumn<IEntityTable>>();
	for (IColumnConfig columnConfig : columnsConfig) {
	    columnConfig.addColumns(columns, releaseURI);
	}

	DataTable<IEntityTable> resultTable = new DataTable<IEntityTable>("datatable", columns, dataProvider,
		(rowsPerPage == null ? 20 : rowsPerPage));
	resultTable.setOutputMarkupId(true);
	resultTable.setVersioned(false);
	resultTable.addTopToolbar(new HeadersToolbar(resultTable, dataProvider));
	resultTable.addBottomToolbar(new NoRecordsToolbar(resultTable));
	resultTable.addBottomToolbar(new AjaxNavigationToolbar(resultTable));
	// resultTable.addBottomToolbar(new NavigationToolbar(resultTable));
	add(resultTable);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
	super.renderHead(response);
	response.renderCSSReference(TABLE_VIEWER_CSS);
    }

}
