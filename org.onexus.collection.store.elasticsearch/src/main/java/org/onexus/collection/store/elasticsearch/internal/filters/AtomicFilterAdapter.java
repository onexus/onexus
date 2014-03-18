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
package org.onexus.collection.store.elasticsearch.internal.filters;

import org.elasticsearch.index.query.FilterBuilder;
import org.onexus.collection.api.query.AtomicFilter;
import org.onexus.collection.api.query.Query;

public abstract class AtomicFilterAdapter<T extends AtomicFilter> extends AbstractFilterAdapter<T> {

    public AtomicFilterAdapter(Class<T> type) {
        super(type);
    }

    @Override
    protected FilterBuilder innerBuild(Query query, T filter) {
        return innerBuild(
                fieldName(query, filter.getCollectionAlias(), filter.getFieldId()),
                filter
        );
    }

    protected abstract FilterBuilder innerBuild(String field, T filter);

}
