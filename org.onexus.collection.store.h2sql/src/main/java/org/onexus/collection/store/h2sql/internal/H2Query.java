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
package org.onexus.collection.store.h2sql.internal;

import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlQuery;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;

import java.util.List;

public class H2Query extends SqlQuery {

    public H2Query(SqlCollectionStore manager, Query query) {
        super(manager, query);
    }

    @Override
    protected void addOrderBy() {

        List<OrderBy> ordersOql = query.getOrders();

        if (ordersOql == null || ordersOql.isEmpty()) {
            return;
        }

        for (OrderBy order : ordersOql) {
            this.orderBy.add("`" + order.getCollection() + "`.`" + order.getField() + "`" + (order.isAscendent() ? " ASC" : "DESC") + " NULLS LAST");
        }
    }
}
