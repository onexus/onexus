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
import org.onexus.collection.api.query.And;
import org.onexus.collection.api.query.Or;

import static org.elasticsearch.index.query.FilterBuilders.orFilter;

public class OrFilterAdapter extends BinaryFilterAdapter<Or> {

    public OrFilterAdapter() {
        super(Or.class);
    }

    @Override
    protected FilterBuilder innerBuild(FilterBuilder left, FilterBuilder right, Or filter) {
        return orFilter(left, right);
    }
}
