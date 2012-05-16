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

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IEntityTable;
import org.onexus.core.TaskStatus;
import org.onexus.core.query.OrderBy;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.IQueryContributor;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;
import org.onexus.ui.website.widgets.tableviewer.headers.FieldHeader;
import org.onexus.ui.workspace.progressbar.ProgressBar;

import javax.inject.Inject;
import java.util.Iterator;

public abstract class EntitiesRowProvider implements
        ISortableDataProvider<IEntityTable, String>, IQueryContributor {

    @Inject
    public ICollectionManager collectionManager;

    private TableViewerConfig config;
    private IModel<TableViewerStatus> statusModel;
    private transient Iterator<IEntityTable> rows;
    private SortState sortState = new SortState();

    public EntitiesRowProvider(TableViewerConfig config,
                               IModel<TableViewerStatus> status) {
        OnexusWebApplication.get().getInjector().inject(this);
        this.statusModel = status;
        this.config = config;
    }

    protected TableViewerStatus getTableViewerStatus() {
        return statusModel.getObject();
    }

    @Override
    public Iterator<IEntityTable> iterator(long first, long total) {

        Query query = getQuery();
        query.setOffset(first);
        query.setCount(total);
        return loadIterator(query);
    }

    @Override
    public long size() {
        Query query = getQuery();
        IEntityTable entityTable = collectionManager.load(query);

        TaskStatus task = entityTable.getTaskStatus();
        if (task != null && !task.isDone()) {
            addTaskStatus(entityTable.getTaskStatus());
        }

        return (int) entityTable.size();
    }

    @Override
    public void onQueryBuild(Query query) {

            BrowserPageStatus status = getBrowserPageStatus();
            String releaseURI = (status == null ? null : status.getReleaseURI());

            query.setOn(releaseURI);
            String collectionAlias = QueryUtils.newCollectionAlias(query, config.getCollection());
            query.setFrom(collectionAlias);

            int currentColumnSet = getTableViewerStatus().getCurrentColumnSet();

            for (IColumnConfig column : config.getColumnSets().get(currentColumnSet).getColumns()) {
                column.buildQuery(query);
            }

        OrderBy orderWithCollection = getTableViewerStatus().getOrder();

        if (orderWithCollection != null) {
            String collectionUri = QueryUtils.getAbsoluteCollectionUri(query, orderWithCollection.getCollectionRef());
            collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
            OrderBy orderWithAlias = new OrderBy(collectionAlias, orderWithCollection.getFieldId(), orderWithCollection.isAscendent());
            query.addOrderBy(orderWithAlias);
        }


    }

    protected abstract Query getQuery();

    protected abstract BrowserPageStatus getBrowserPageStatus();

    protected abstract void addTaskStatus(TaskStatus taskStatus);


    private Iterator<IEntityTable> loadIterator(Query query) {
        if (rows == null) {
            IEntityTable entityTable = ProgressBar.show(collectionManager.load(query));

            TaskStatus task = entityTable.getTaskStatus();
            if (task != null && !task.isDone()) {
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
            return collectionManager.load(query);
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

    public class SortState implements ISortState<String> {

        @Override
        public void setPropertySortOrder(String property, SortOrder order) {
            String[] values = property
                    .split(FieldHeader.SORT_PROPERTY_SEPARATOR);
            String collectionId = values[0];
            String fieldName = values[1];

            getTableViewerStatus().setOrder(
                    new OrderBy(collectionId, fieldName, order == SortOrder.ASCENDING));

        }

        @Override
        public SortOrder getPropertySortOrder(String property) {
            String[] values = property
                    .split(FieldHeader.SORT_PROPERTY_SEPARATOR);
            String collectionId = values[0];
            String fieldId = values[1];

            OrderBy order = getTableViewerStatus().getOrder();
            if (order != null && order.getCollectionRef().equals(collectionId)
                    && order.getFieldId().equals(fieldId)) {
                return (order.isAscendent() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
            }

            return SortOrder.NONE;
        }

    }

}
