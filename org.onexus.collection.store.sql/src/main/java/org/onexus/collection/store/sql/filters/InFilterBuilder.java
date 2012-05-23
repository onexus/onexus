package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionDDL;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.query.In;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;

import java.util.Iterator;


public class InFilterBuilder extends AbstractFilterBuilder<In> {

    public InFilterBuilder(SqlDialect dialect ) {
        super(dialect, In.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, In filter) {

        // Collection
        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        SqlCollectionDDL collection = store.getDDL(collectionUri);

        // Field
        String fieldId = filter.getFieldId();
        SqlCollectionDDL.ColumnInfo column = collection.getColumnInfoByFieldName(fieldId);

        // Values
        Iterator<Object>  values = filter.getValues().iterator();

        where.append('`').append(collectionAlias).append("`.`").append(column.getColumnName()).append('`');
        where.append(" IN (");

        while (values.hasNext()) {
            encodeValue(where, column.getField().getType(), values.next());

            if (values.hasNext()) {
                where.append(',');
            }
        }
        where.append(")");

    }
}
