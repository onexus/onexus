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

import org.onexus.core.query.OrderBy;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

public class TableViewerStatus extends WidgetStatus<TableViewerConfig> {

    private OrderBy order;
    
    private int currentColumnSet;

    public TableViewerStatus() {
        super();
    }

    public TableViewerStatus(String viewerId) {
        super(viewerId);
    }

    public TableViewerStatus(String viewerId, OrderBy order) {
        super(viewerId);
        this.order = order;
    }

    public OrderBy getOrder() {
        return order;
    }

    public void setOrder(OrderBy order) {
        this.order = order;
    }

    public int getCurrentColumnSet() {
        return currentColumnSet;
    }

    public void setCurrentColumnSet(int currentColumnSet) {
        this.currentColumnSet = currentColumnSet;
    }

    @Override
    public void beforeQueryBuild(Query query) {

        String collectionAlias = QueryUtils.newCollectionAlias(query, getConfig().getCollection());
        query.setFrom(collectionAlias);

    }

    @Override
    public void afterQueryBuild(Query query) {

    }

    @Override
    public void onQueryBuild(Query query) {

        int currentColumnSet = getCurrentColumnSet();

        for (IColumnConfig column : getConfig().getColumnSets().get(currentColumnSet).getColumns()) {
            column.buildQuery(query);
        }

        OrderBy orderWithCollection = getOrder();

        if (orderWithCollection != null) {
            String collectionUri = QueryUtils.getAbsoluteCollectionUri(query, orderWithCollection.getCollectionRef());
            String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
            OrderBy orderWithAlias = new OrderBy(collectionAlias, orderWithCollection.getFieldId(), orderWithCollection.isAscendent());
            query.addOrderBy(orderWithAlias);
        }

    }
}
