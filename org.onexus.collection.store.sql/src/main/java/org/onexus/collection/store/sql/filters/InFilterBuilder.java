package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.In;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

import java.util.Iterator;


public class InFilterBuilder extends AbstractFilterBuilder<In> {

    public InFilterBuilder(SqlDialect dialect ) {
        super(dialect, In.class);
    }

    @Override
    protected void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, In filter) {

        // Collection
        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        Collection collection = resourceManager.load(Collection.class, collectionUri);

        // Field
        String fieldId = filter.getFieldId();
        Field field = collection.getField(fieldId);

        // Values
        Iterator<Object>  values = filter.getValues().iterator();

        where.append('`').append(collectionAlias).append("`.`").append(fieldId).append('`');
        where.append(" IN (");

        while (values.hasNext()) {
            encodeValue(where, field.getType(), values.next());

            if (values.hasNext()) {
                where.append(',');
            }
        }
        where.append(")");

    }
}
