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
import org.onexus.collection.api.query.IsNull;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;


public class IsNullFilterBuilder extends AbstractFilterBuilder<IsNull> {

    public IsNullFilterBuilder(SqlDialect dialect) {
        super(dialect, IsNull.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, IsNull filter) {

        // Collection
        String collectionAlias = filter.getCollectionAlias();
        ORI collectionUri = QueryUtils.getCollectionOri(query, collectionAlias);
        SqlCollectionDDL collection = store.getDDL(collectionUri);
        SqlCollectionDDL.ColumnInfo column = collection.getColumnInfoByFieldName(filter.getFieldId());

        where.append("`").append(filter.getCollectionAlias()).append("`.`").append(column.getColumnName()).append("`");
        where.append(" IS NULL");
    }
}
