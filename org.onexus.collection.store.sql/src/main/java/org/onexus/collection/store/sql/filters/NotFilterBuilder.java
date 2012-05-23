package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Not;
import org.onexus.core.query.Query;


public class NotFilterBuilder extends AbstractFilterBuilder<Not> {

    public NotFilterBuilder(SqlDialect dialect) {
        super(dialect, Not.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, Not filter) {

        Filter negatedFilter = filter.getNegatedFilter();
        FilterBuilder negatedFilterBuilder = getDialect().getFilterBuilder(negatedFilter);

        where.append("NOT ");
        negatedFilterBuilder.build(store, query, where, negatedFilter);

    }
}
