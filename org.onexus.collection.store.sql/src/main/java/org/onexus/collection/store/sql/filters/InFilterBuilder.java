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
