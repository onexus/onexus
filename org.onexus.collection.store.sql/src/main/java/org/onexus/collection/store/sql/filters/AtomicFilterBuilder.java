package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.AtomicFilter;
import org.onexus.core.query.Contains;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

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
    protected void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, AtomicFilter filter) {

        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        Collection collection = resourceManager.load(Collection.class, collectionUri);

        where.append('`').append( filter.getCollectionAlias()).append("`.`").append(filter.getFieldId()).append('`');

        String operator = oqlToSql.get(filter.getOperandSymbol());
        if (operator == null) {
            operator = filter.getOperandSymbol();
        }



        Field field = collection.getField(filter.getFieldId());

        where.append(' ').append(operator).append(' ');
        if (filter instanceof Contains) {
            encodeValue(where, String.class, "%" + String.valueOf(filter.getValue()) + "%");
        } else {
            encodeValue(where, field.getType(), filter.getValue());
        }

    }
}
