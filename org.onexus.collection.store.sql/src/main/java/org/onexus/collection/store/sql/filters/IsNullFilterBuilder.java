package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionDDL;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.query.IsNull;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;


public class IsNullFilterBuilder extends AbstractFilterBuilder<IsNull> {

    public IsNullFilterBuilder(SqlDialect dialect) {
        super(dialect, IsNull.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, IsNull filter) {

        // Collection
        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        SqlCollectionDDL collection = store.getDDL(collectionUri);
        SqlCollectionDDL.ColumnInfo column = collection.getColumnInfoByFieldName(filter.getFieldId());

        where.append("`").append(filter.getCollectionAlias()).append("`.`").append(column.getColumnName()).append("`");
        where.append(" IS NULL");
    }
}
