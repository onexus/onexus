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
package org.onexus.website.widget.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.In;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class InListOperation extends FilterOperation {

    public static InListOperation INSTANCE = new InListOperation();

    private InListOperation() {
        super("in list", "in", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {

        In filter = new In(alias, fieldId);
        for (String val : parseValues(value)) {
            filter.addValue(val.trim());
        }

        return filter;
    }

    private List<String> parseValues(Object value) {

        if (value == null) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(String.valueOf(value).split(","));
    }

    @Override
    public String createTitle(String headerTitle, Object value) {

        StringBuilder title = new StringBuilder();

        title.append(headerTitle);
        title.append(" ");
        title.append(getSymbol());
        title.append(" (");

        Iterator<String> values = parseValues(value).iterator();

        while (values.hasNext()) {
            title.append("'").append(values.next().trim()).append("'");
            if (values.hasNext()) {
                title.append(",");
            }
        }

        title.append(")");
        return title.toString();
    }
}
