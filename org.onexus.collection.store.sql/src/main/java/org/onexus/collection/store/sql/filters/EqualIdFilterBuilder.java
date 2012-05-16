package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.EqualId;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

import java.util.ArrayList;
import java.util.List;

public class EqualIdFilterBuilder extends AbstractFilterBuilder<EqualId> {

    public EqualIdFilterBuilder(SqlDialect dialect) {
        super(dialect, EqualId.class);
    }


    @Override
    protected void innerBuild(IResourceManager resourceManager, Query query, StringBuilder where, EqualId filter) {

        String collectionAlias = filter.getCollectionAlias();
        String collectionUri = QueryUtils.getCollectionUri(query, collectionAlias);
        Collection collection = resourceManager.load(Collection.class, collectionUri);


        List<Field> primaryKey = new ArrayList<Field>();
        for (Field field : collection.getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                primaryKey.add(field);
            }
        }

        String ids[] = filter.getId().split("\t");

        if (primaryKey.size() != ids.length) {
            throw new RuntimeException("Error on filter: " + filter);
        }

        if (ids.length > 1) {
            where.append("(");
        }

        for (int i = 0; i < ids.length; i++) {

            Field keyField = primaryKey.get(i);


            where.append("`").append(collectionAlias).append("`.`");
            where.append(keyField.getId()).append("` = ");

            encodeValue(where, keyField.getType(), ids[i]);

            if (i + 1 < ids.length) {
                where.append(" AND ");
            }
        }

        if (ids.length > 1) {
            where.append(")");
        }


    }
}
