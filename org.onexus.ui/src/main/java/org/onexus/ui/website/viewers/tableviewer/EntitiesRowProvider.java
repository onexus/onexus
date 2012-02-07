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

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.Order;
import org.onexus.core.query.Query;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.viewers.tableviewer.columns.IColumnConfig;
import org.onexus.ui.website.viewers.tableviewer.headers.FieldHeader;

public abstract class EntitiesRowProvider implements
	ISortableDataProvider<IEntityTable> {

    private TableViewerConfig config;
    private IModel<TableViewerStatus> statusModel;
    private transient Iterator<IEntityTable> rows;
    private SortState sortState = new SortState();

    public EntitiesRowProvider(TableViewerConfig config,
	    IModel<TableViewerStatus> status) {
	this.statusModel = status;
	this.config = config;
    }

    protected TableViewerStatus getTableViewerStatus() {
	TableViewerStatus status = statusModel.getObject();
	
	if (status == null) {
	    status = config.getDefaultStatus();
	    statusModel.setObject(status);
	}
	
	if (status == null) {
	    status = config.createEmptyStatus();
	    statusModel.setObject(status);
	}
	
	return status;
    }

    @Override
    public Iterator<IEntityTable> iterator(int first, int total) {

	Query query = loadSort(buildQuery());
	query.setFirstResult(first);
	query.setMaxResults(total);
	return loadIterator(query);
    }

    @Override
    public int size() {
	Query query = buildQuery();
	IEntityTable entityTable = OnexusWebSession.get().getCollectionManager().load(query);
	
	TaskStatus task = entityTable.getTaskStatus(); 
	if (task!=null && !task.isDone()) {
	    addTaskStatus(entityTable.getTaskStatus());
	}
	
	return (int) entityTable.size();
    }

    private Query buildQuery() {

	
	Query query = new Query(config.getMainCollection());
	
	BrowserPageStatus status = getBrowserPageStatus();
	String releaseURI = (status==null?null:status.getReleaseURI());
	query.setMainNamespace(releaseURI);
	
	for (IColumnConfig column : config.getColumns()) {
	    for (String collectionId : column.getQueryCollections(releaseURI)) {
		query.getCollections().add(collectionId);
	    }
	}

	buildQuery(query);

	return query;
    }

    protected abstract void buildQuery(Query query);
    
    protected abstract BrowserPageStatus getBrowserPageStatus();
    
    protected abstract void addTaskStatus(TaskStatus taskStatus);
    
    private Query loadSort(Query query) {
	Order order = getTableViewerStatus().getOrder();
	if (order != null) {
	    query.setOrder(order);
	}
	return query;
    }

    private Iterator<IEntityTable> loadIterator(Query query) {
	if (rows == null) {
	    IEntityTable entityTable = OnexusWebSession.get().getCollectionManager().load(query);
	    
	    TaskStatus task = entityTable.getTaskStatus(); 
	    if (task!=null && !task.isDone()) {
		addTaskStatus(entityTable.getTaskStatus());
	    }
	    
	    rows = new EntitiesRow(entityTable);
	}
	return rows;
    }

    @Override
    public IModel<IEntityTable> model(IEntityTable object) {
	return new EntityMatrixModel(object);
    }

    @Deprecated
    public class EntityMatrixModel extends
	    LoadableDetachableModel<IEntityTable> {

	private Query query;

	public EntityMatrixModel(IEntityTable matrix) {
	    super(matrix);
	    this.query = matrix.getQuery();
	}

	@Override
	protected IEntityTable load() {
	    return OnexusWebSession.get().getCollectionManager()
		    .load(query);
	}

    }

    @Override
    public void detach() {
	if (rows != null) {
	    statusModel.detach();
	    rows = null;
	}
    }

    @Override
    public ISortState getSortState() {
	return sortState;
    }

    public class SortState implements ISortState {

	@Override
	public void setPropertySortOrder(String property, SortOrder order) {
	    String[] values = property
		    .split(FieldHeader.SORT_PROPERTY_SEPARATOR);
	    String collectionId = values[0];
	    String fieldName = values[1];

	    getTableViewerStatus().setOrder(
		    new Order(collectionId, fieldName,
			    order == SortOrder.ASCENDING));

	}

	@Override
	public SortOrder getPropertySortOrder(String property) {
	    String[] values = property
		    .split(FieldHeader.SORT_PROPERTY_SEPARATOR);
	    String collectionId = values[0];
	    String fieldName = values[1];

	    
	    Order order = getTableViewerStatus().getOrder();
	    if (order != null && order.getCollection().equals(collectionId)
		    && order.getFieldName().equals(fieldName)) {
		return (order.isAscending() ? SortOrder.ASCENDING
			: SortOrder.DESCENDING);
	    }

	    return SortOrder.NONE;
	}

    }

}
