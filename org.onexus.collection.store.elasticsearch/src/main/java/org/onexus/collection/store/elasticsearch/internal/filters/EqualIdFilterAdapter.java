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
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import java.util.regex.Pattern;

import static org.elasticsearch.index.query.FilterBuilders.idsFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.ENTITY_TYPE;

public class EqualIdFilterAdapter extends AbstractFilterAdapter<EqualId> {

    private static Pattern SEPARATOR_PATTERN = Pattern.compile( Pattern.quote( ElasticSearchUtils.PRIMARY_KEY_SEPARATOR ));

    public EqualIdFilterAdapter() {
        super(EqualId.class);
    }

    @Override
    protected FilterBuilder innerBuild(IResourceManager resourceManager, Query query, EqualId filter) {

        String id = String.valueOf(filter.getId());
        String collectionAlias = filter.getCollectionAlias();

        if (isFromCollection(query, collectionAlias)) {
            return idsFilter(ENTITY_TYPE).ids(id);
        }

        String indexName = indexName(query, collectionAlias);
        ORI collectionOri = query.getDefine().get(collectionAlias).toAbsolute(query.getOn());
        Collection collection = resourceManager.load(Collection.class, collectionOri);

        int i = 0;
        FilterBuilder filterBuilder = null;
        String values[] = SEPARATOR_PATTERN.split(id);
        for (Field field : collection.getFields()) {
            if (Boolean.TRUE.equals(field.isPrimaryKey())) {
                FilterBuilder fieldFilter = FilterBuilders.termFilter(indexName + "." + field.getId(), toLowerCase(values[i]));
                filterBuilder = (filterBuilder == null ? fieldFilter : FilterBuilders.andFilter(filterBuilder, fieldFilter));
                i++;
            }
        }

        return filterBuilder;
    }
}
