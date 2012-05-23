package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;

public interface FilterBuilder {

    boolean canBuild(Filter filter);

    void build(SqlCollectionStore store, Query query, StringBuilder where, Filter filter);
}
