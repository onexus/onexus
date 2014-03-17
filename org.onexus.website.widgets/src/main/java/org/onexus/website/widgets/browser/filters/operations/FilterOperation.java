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
package org.onexus.website.widgets.browser.filters.operations;

import org.onexus.collection.api.query.Filter;

import java.io.Serializable;

public abstract class FilterOperation implements Serializable {

    private String label;
    private String symbol;
    private boolean needsValue;

    protected FilterOperation(String label, String symbol, boolean needsValue) {
        this.label = label;
        this.symbol = symbol;
        this.needsValue = needsValue;
    }

    public String getLabel() {
        return label;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isNeedsValue() {
        return needsValue;
    }

    public String toString() {
        return label;
    }

    public abstract Filter createFilter(String alias, String fieldId, Object value);

    public String createTitle(String headerTitle, Object value) {
        return headerTitle + " " + symbol + (needsValue ? " '" + value + "'" : "");
    }
}
