package org.onexus.collection.store.sql.filters;

import org.onexus.core.IResourceManager;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;

public class UnknownFilterBuilder implements FilterBuilder {

    private Filter filter;

    public UnknownFilterBuilder(Filter filter) {
         this.filter = filter;
    }

    @Override
    public boolean canBuild(Filter filter) {
        return false;
    }

    @Override
    public void build(IResourceManager resourceManager, Query query, StringBuilder where, Filter filter) {
        where.append("NOT ISNULL(\" ERROR: Unknown filter -- ");
        filter.toString(where, false);
        where.append(" -- \")");
    }
}
