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
package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;

public class EqualOperation extends FilterOperation {

    public static EqualOperation INSTANCE = new EqualOperation();

    private EqualOperation() {
        super("equal", "=", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new Equal(alias, fieldId, value);
    }
}
