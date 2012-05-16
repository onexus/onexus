package org.onexus.collection.store.sql.filters;

import org.onexus.core.IResourceManager;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;

public interface FilterBuilder {

    boolean canBuild(Filter filter);

    void build(IResourceManager resourceManager, Query query, StringBuilder where, Filter filter);
}
