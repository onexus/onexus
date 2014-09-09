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
package org.onexus.collection.api.query;

/**
 * This filter, filters out the collection 'collectionAlias' entities that the 'fieldId'
 * is not greater than or equal to the given 'value'.
 */
public class GreaterThanOrEqual extends AtomicFilter {

    @SuppressWarnings("UnusedDeclaration")
    public GreaterThanOrEqual() {
        // Keep this constructor for JAXB compatibility
    }

    /**
     * Create a GREATER OR EQUAL THAN filter.
     *
     * @param collectionAlias The collection to filter.
     * @param fieldId The field id to filter.
     * @param value The value to use when comparing values.
     */
    public GreaterThanOrEqual(String collectionAlias, String fieldId, Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public String getOperandSymbol() {
        return ">=";
    }
}
