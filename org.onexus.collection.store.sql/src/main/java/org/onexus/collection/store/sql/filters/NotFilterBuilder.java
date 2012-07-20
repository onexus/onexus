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

import org.onexus.collection.store.sql.SqlCollectionStore;
import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Not;
import org.onexus.collection.api.query.Query;


public class NotFilterBuilder extends AbstractFilterBuilder<Not> {

    public NotFilterBuilder(SqlDialect dialect) {
        super(dialect, Not.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store, Query query, StringBuilder where, Not filter) {

        Filter negatedFilter = filter.getNegatedFilter();
        FilterBuilder negatedFilterBuilder = getDialect().getFilterBuilder(negatedFilter);

        where.append("NOT ");
        negatedFilterBuilder.build(store, query, where, negatedFilter);

    }
}
