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
import org.elasticsearch.index.query.FilterBuilders;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilterAdapter<T extends Filter> implements FilterAdapter {

    private static final Logger log = LoggerFactory.getLogger(AbstractFilterAdapter.class);
    private Class<T> filterClass;

    public AbstractFilterAdapter(Class<T> filterClass) {
        this.filterClass = filterClass;
    }

    @Override
    public boolean canBuild(Filter filter) {

        if (filter == null) {
            return false;
        }

        return filterClass.isAssignableFrom(filter.getClass());
    }

    @Override
    public FilterBuilder build(ElasticSearchQuery query, Filter filter) {
        return innerBuild(query, (T) filter);
    }

    protected FilterBuilder innerBuild(ElasticSearchQuery query, T filter) {
        return FilterBuilders.matchAllFilter();
    }

    protected Object toLowerCase(Object value) {
        if (value instanceof String) {
            value = String.class.cast(value).toLowerCase();
        }
        return value;
    }
}
