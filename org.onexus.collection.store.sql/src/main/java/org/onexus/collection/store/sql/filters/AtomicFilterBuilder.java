package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionDDL;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.query.AtomicFilter;
import org.onexus.core.query.Contains;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;

import java.util.HashMap;
import java.util.Map;

public class AtomicFilterBuilder extends AbstractFilterBuilder<AtomicFilter> {

    private final static Map<String, String> oqlToSql = new HashMap<String, String>();

    static {

        oqlToSql.put("CONTAINS", "LIKE");
        oqlToSql.put("=", "=");
        oqlToSql.put(">", ">");
        oqlToSql.put(">=", ">=");
        oqlToSql.put("<", "<");
        oqlToSql.put("<=", "<=");
        oqlToSql.put("!=", "<>");

    }

    public AtomicFilterBuilder(SqlDialect dialect) {
        super(dialect, AtomicFilter.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, AtomicFilter filter) {

        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        SqlCollectionDDL collection = store.getDDL(collectionUri);

        SqlCollectionDDL.ColumnInfo column = collection.getColumnInfoByFieldName(filter.getFieldId());

        where.append('`').append( filter.getCollectionAlias()).append("`.`").append(column.getColumnName()).append('`');

        String operator = oqlToSql.get(filter.getOperandSymbol());
        if (operator == null) {
            operator = filter.getOperandSymbol();
        }

        where.append(' ').append(operator).append(' ');
        if (filter instanceof Contains) {
            encodeValue(where, String.class, "%" + String.valueOf(filter.getValue()) + "%");
        } else {
            encodeValue(where, column.getField().getType(), filter.getValue());
        }

    }
}
