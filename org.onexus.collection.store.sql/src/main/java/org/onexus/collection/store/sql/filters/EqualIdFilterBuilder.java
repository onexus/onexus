/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.collection.store.sql.filters;

import org.onexus.collection.store.sql.SqlCollectionDDL;
import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;

import java.util.ArrayList;
import java.util.List;

public class EqualIdFilterBuilder extends AbstractFilterBuilder<EqualId> {

    public EqualIdFilterBuilder(SqlDialect dialect) {
        super(dialect, EqualId.class);
    }


    @Override
    protected void innerBuild(SqlCollectionStore store,  Query query, StringBuilder where, EqualId filter) {

        String collectionAlias = filter.getCollectionAlias();
        ORI collectionUri = QueryUtils.getCollectionOri(query, collectionAlias);
        SqlCollectionDDL collection = store.getDDL(collectionUri);

        List<Field> primaryKey = new ArrayList<Field>();
        for (Field field : collection.getCollection().getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                primaryKey.add(field);
            }
        }

        Object id = filter.getId();
        Object[] ids = String.valueOf(id).split("\t");

        if (primaryKey.size() != ids.length) {
            throw new RuntimeException("Error on filter: " + filter);
        }

        if (ids.length > 1) {
            where.append("(");
        }

        for (int i = 0; i < ids.length; i++) {

            Field keyField = primaryKey.get(i);

            String columnName = collection.getColumnInfoByFieldName(keyField.getId()).getColumnName();

            where.append("`").append(collectionAlias).append("`.`");
            where.append(columnName).append("` = ");

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
