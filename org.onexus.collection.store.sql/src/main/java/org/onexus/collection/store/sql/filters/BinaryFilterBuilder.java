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
import org.onexus.core.query.BinaryFilter;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;

public class BinaryFilterBuilder extends AbstractFilterBuilder<BinaryFilter> {

    public BinaryFilterBuilder(SqlDialect dialect) {
        super(dialect, BinaryFilter.class);
    }

    @Override
    protected void innerBuild(SqlCollectionStore store,  Query query, StringBuilder where, BinaryFilter filter) {

        Filter leftFilter = filter.getLeft();
        Filter rightFilter = filter.getRight();

        FilterBuilder leftFilterBuilder = getDialect().getFilterBuilder(leftFilter);
        FilterBuilder rightFilterBuilder = getDialect().getFilterBuilder(rightFilter);

        where.append("(");
        leftFilterBuilder.build(store, query, where, leftFilter);
        where.append(' ').append(filter.getOperandSymbol()).append(' ');
        rightFilterBuilder.build(store, query, where, rightFilter);
        where.append(")");
    }
}
