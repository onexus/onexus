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
 * This filter, filters out the collection 'collectionAlias' entities that the 'fieldId' don't
 * contains a full or partial match of the given 'value'.
 */
public class Contains extends AtomicFilter {

    /**
     * Keep this constructor for JAXB compatibility.
     */
    @SuppressWarnings("UnusedDeclaration")
    public Contains() {
        super();
    }

    /**
     * Create a CONTAINS filter.
     *
     * @param collectionAlias The collection alias to filter.
     * @param fieldId         The field id to filter.
     * @param value           The value to match.
     */
    public Contains(final String collectionAlias, final String fieldId, final Object value) {
        super(collectionAlias, fieldId, value);
    }

    @Override
    public final String getOperandSymbol() {
        return "CONTAINS";
    }
}
