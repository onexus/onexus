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

import com.google.common.base.Function;
import org.elasticsearch.index.query.FilterBuilder;
import org.onexus.collection.api.query.In;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchQuery;

import static com.google.common.collect.Iterables.transform;
import static org.elasticsearch.index.query.FilterBuilders.termsFilter;


public class InFilterAdapter extends AbstractFilterAdapter<In> {

    private Function<Object, Object> TO_LOWER_CASE;

    public InFilterAdapter() {
        super(In.class);

        TO_LOWER_CASE = new Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                return InFilterAdapter.this.toLowerCase(input);
            }
        };
    }

    @Override
    protected FilterBuilder innerBuild(ElasticSearchQuery query, In filter) {
        return termsFilter(
                query.fieldPath(filter.getCollectionAlias(), filter.getFieldId()),
                transform(filter.getValues(), TO_LOWER_CASE)
        );
    }
}
