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
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.convertOriToIndexName;

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
    public FilterBuilder build(IResourceManager resourceManager, Query query, Filter filter) {
        return innerBuild(resourceManager, query, (T) filter);
    }

    protected FilterBuilder innerBuild(IResourceManager resourceManager, Query query, T filter) {
        return innerBuild(query, filter);
    }

    protected FilterBuilder innerBuild(Query query, T filter) {
        return FilterBuilders.matchAllFilter();
    }

    protected boolean isFromCollection(Query query, String collectionAlias) {
        return query.getFrom().equals(collectionAlias);
    }

    protected String fieldName(Query query, String collectionAlias, String fieldId) {

        if (isFromCollection(query, collectionAlias)) {
            return fieldId;
        }

        return indexName(query, collectionAlias) + "." + fieldId;
    }

    protected String indexName(Query query, String collectionAlias) {
        ORI ori = query.getDefine().get(collectionAlias).toAbsolute(query.getOn());
        return convertOriToIndexName(ori);
    }

    protected Object toLowerCase(Object value) {
        if (value instanceof String) {
            value = String.class.cast(value).toLowerCase();
        }
        return value;
    }
}
