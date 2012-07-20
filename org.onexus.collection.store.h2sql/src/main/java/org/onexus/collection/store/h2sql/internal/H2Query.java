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
            this.orderBy.add("`" + order.getCollectionRef() + "`.`" + order.getFieldId() + "`" + (order.isAscendent() ? " ASC" : "DESC") + " NULLS LAST");
        }
    }
}
