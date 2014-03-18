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

import org.onexus.collection.api.query.Filter;

import java.util.Arrays;
import java.util.List;

public class FilterAdapterFactory {

    private static final List<? extends FilterAdapter> BUILDERS = Arrays.asList(
            new AndFilterAdapter(),
            new ContainsFilterAdapter(),
            new EqualFilterAdapter(),
            new EqualIdFilterAdapter(),
            new GreaterThanFilterAdapter(),
            new GreaterThanOrEqualFilterAdapter(),
            new InFilterAdapter(),
            new IsNullFilterAdapter(),
            new LessThanFilterAdapter(),
            new LessThanOrEqualFilterAdapter(),
            new NotEqualFilterAdapter(),
            new NotFilterAdapter(),
            new OrFilterAdapter()
    );

    public static FilterAdapter filterAdapter(Filter filter) {

        for (FilterAdapter builder : BUILDERS) {
            if (builder.canBuild(filter)) {
                return builder;
            }
        }

        throw new UnsupportedOperationException("Unsupported query filter '" + filter + "'");
    }
}
