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
import org.onexus.resource.api.ICollectionManager;
import org.onexus.resource.api.IEntityTable;
import org.onexus.resource.api.TaskStatus;
import org.onexus.resource.api.query.OrderBy;
import org.onexus.resource.api.query.Query;
import org.onexus.ui.core.OnexusWebApplication;
import org.onexus.ui.website.widgets.tableviewer.headers.FieldHeader;
import org.onexus.ui.core.progressbar.ProgressBar;

import javax.inject.Inject;
import java.util.Iterator;

public abstract class EntitiesRowProvider implements
        ISortableDataProvider<IEntityTable, String> {

    @Inject
    public ICollectionManager collectionManager;

    private TableViewerConfig config;
    private IModel<TableViewerStatus> statusModel;
    private transient EntitiesRow rows;
    private SortState sortState = new SortState();
    private int rowsPerPage;
    private long knownSize;
    private long realSize;

    public EntitiesRowProvider(TableViewerConfig config,
                               IModel<TableViewerStatus> status, int rowsPerPage) {
        OnexusWebApplication.inject(this);
        this.statusModel = status;
        this.config = config;
        this.rowsPerPage = rowsPerPage;
        this.knownSize = rowsPerPage + 2;
        this.realSize = -1;
    }

    protected TableViewerStatus getTableViewerStatus() {
        return statusModel.getObject();
    }

    public void close() {
        if (rows != null) {
            rows.close();
        }
    }

    @Override
    public Iterator<IEntityTable> iterator(long first, long total) {

        Query query = getQuery();
        query.setOffset(first);
        query.setCount(total);
        return new SpyIterator<IEntityTable>(first, total, loadIterator(query));
    }

    public class SpyIterator<T> implements Iterator<T> {

        private long first, total;
        private Iterator<T> it;

        private long currentPos;

        public SpyIterator(long first, long total, Iterator<T> it) {
            this.first = first;
            this.it = it;
            this.total = total;
            this.currentPos = first - 1;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = it.hasNext();
            if (!hasNext && (currentPos - first < total)) {
                EntitiesRowProvider.this.knownSize = currentPos + 1;
                EntitiesRowProvider.this.realSize = currentPos + 1;
            }
            return hasNext;
        }

        @Override
        public T next() {
            currentPos++;
            return it.next();
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    @Override
    public long size() {
        if (realSize != -1) {
            return realSize;
        }

        return knownSize;
    }

    public void clearCount() {
        this.realSize = -1;
        this.knownSize = rowsPerPage + 2;
    }

    public void forceCount() {

        if (realSize == -1) {
            Query query = getQuery();
            IEntityTable entityTable = collectionManager.load(query);

            TaskStatus task = entityTable.getTaskStatus();
            if (task != null && !task.isDone()) {
                addTaskStatus(entityTable.getTaskStatus());
            }

            this.realSize = entityTable.size();
            this.knownSize = this.realSize;
        }
    }

    public long getRealSize() {
        return realSize;
    }

    public void setRealSize(long realSize) {
        this.realSize = realSize;
    }

    public long getKnownSize() {
        return knownSize;
    }

    public void setKnownSize(long knownSize) {
        this.knownSize = knownSize;
    }

    protected abstract Query getQuery();

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

            EntitiesRowProvider.this.realSize = -1;
            EntitiesRowProvider.this.knownSize = EntitiesRowProvider.this.rowsPerPage + 2;

            getTableViewerStatus().setOrder(new OrderBy(collectionId, fieldName, order == SortOrder.ASCENDING));

        }

        @Override
        public SortOrder getPropertySortOrder(String property) {
            String[] values = property.split(FieldHeader.SORT_PROPERTY_SEPARATOR);
            String collectionId = values[0];
            String fieldId = values[1];

            OrderBy order = getTableViewerStatus().getOrder();
            if (order != null && collectionId.endsWith(order.getCollectionRef()) && order.getFieldId().equals(fieldId)) {
                return (order.isAscendent() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
            }

            return SortOrder.NONE;
        }

    }

}
