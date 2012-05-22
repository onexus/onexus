package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.BinaryFilter;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;

public class BinaryFilterBuilder extends AbstractFilterBuilder<BinaryFilter> {

    public BinaryFilterBuilder(SqlDialect dialect) {
        super(dialect, BinaryFilter.class);
    }

    @Override
    protected void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, BinaryFilter filter) {

        Filter leftFilter = filter.getLeft();
        Filter rightFilter = filter.getRight();

        FilterBuilder leftFilterBuilder = getDialect().getFilterBuilder(leftFilter);
        FilterBuilder rightFilterBuilder = getDialect().getFilterBuilder(rightFilter);

        where.append("(");
        leftFilterBuilder.build(resourceManager, query, where, leftFilter);
        where.append(' ').append(filter.getOperandSymbol()).append(' ');
        rightFilterBuilder.build(resourceManager, query, where, rightFilter);
        where.append(")");
    }
}