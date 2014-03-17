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

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.widget.WidgetStatus;
import org.onexus.website.widget.tableviewer.columns.IColumnConfig;

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
    public void onQueryBuild(Query query) {

        int currentColumnSet = getCurrentColumnSet();

        for (IColumnConfig column : getConfig().getColumnSets().get(currentColumnSet).getColumns()) {
            column.buildQuery(query);
        }

        OrderBy orderWithCollection = getOrder();

        if (orderWithCollection != null) {
            ORI collectionUri = new ORI(orderWithCollection.getCollection()).toAbsolute(query.getOn());
            String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
            OrderBy orderWithAlias = new OrderBy(collectionAlias, orderWithCollection.getField(), orderWithCollection.isAscendent());
            query.addOrderBy(orderWithAlias);
        }
    }

    @Override
    public void encodeParameters(PageParameters parameters, String keyPrefix) {

        WebsiteConfig websiteConfig = getConfig().getWebsiteConfig();
        ORI projectUri = websiteConfig == null ? null : websiteConfig.getORI().getParent();

        TableViewerStatus defaultStatus = getConfig().getDefaultStatus();
        if (defaultStatus == null) {
            defaultStatus = getConfig().createEmptyStatus();
        }

        if (currentColumnSet != defaultStatus.currentColumnSet) {
            parameters.add(keyPrefix + "cs", currentColumnSet);
        }

        if (order != null && !order.equals(defaultStatus.getOrder())) {
            parameters.add(keyPrefix + "o", new ORI(order.getCollection()).toRelative(projectUri).toString() + "::" + order.getField() + "::" + (order.isAscendent() ? "a" : "d"));
        }

        super.encodeParameters(parameters, keyPrefix);
    }

    @Override
    public void decodeParameters(PageParameters parameters, String keyPrefix) {

        StringValue cs = parameters.get(keyPrefix + "cs");

        if (!cs.isEmpty()) {
            currentColumnSet = Integer.parseInt(cs.toString("0"));
        }

        StringValue o = parameters.get(keyPrefix + "o");

        if (!o.isEmpty()) {
            String[] values = o.toString().split("::");

            String collection = values[0];
            if (collection.charAt(0) == '/') {
                collection = collection.substring(1);
            }

            order = new OrderBy(collection, values[1], values[2].contains("a"));
        }

        super.decodeParameters(parameters, keyPrefix);
    }
}
