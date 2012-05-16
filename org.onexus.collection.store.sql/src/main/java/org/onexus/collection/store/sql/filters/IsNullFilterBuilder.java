package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.IsNull;
import org.onexus.core.query.Query;


public class IsNullFilterBuilder extends AbstractFilterBuilder<IsNull> {

    public IsNullFilterBuilder(SqlDialect dialect) {
        super(dialect, IsNull.class);
    }

    @Override
    protected void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, IsNull filter) {
         where.append("`").append(filter.getCollectionAlias()).append("`.`").append(filter.getFieldId()).append("`");
         where.append(" IS NULL");
    }
}
