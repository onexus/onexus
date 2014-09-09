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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This filter, filters out the collection 'collectionAlias' entities that the 'fieldId' don't
 * match any of the given values.
 */
public class In extends Filter {

    private String collectionAlias;

    private String fieldId;

    private Set<Object> values = new HashSet<Object>();

    @SuppressWarnings("UnusedDeclaration")
    public In() {
        // Keep this constructor for JAXB compatibility
    }

    /**
     * Create a IN filter.
     *
     * @param collectionAlias The collection to filter.
     * @param fieldId The field id to filter.
     *
     */
    public In(String collectionAlias, String fieldId) {
        super();
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
    }

    /**
     * The filtering collection.
     *
     * @return The collection alias.
     */
    public String getCollectionAlias() {
        return collectionAlias;
    }

    /**
     * Sets the collection to filter.
     *
     * @param collectionAlias The collection alias.
     */
    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    /**
     * The filtering field
     *
     * @return The filed id.
     */
    public String getFieldId() {
        return fieldId;
    }

    /**
     * Sets the field to filter.
     *
     * @param fieldId The field id.
     */
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Adds one valid value
     *
     * @param value
     */
    public void addValue(Object value) {
        this.values.add(value);
    }

    /**
     * All valid values.
     *
     * @return A <code>Set</code> with all the valid values.
     */
    public Set<Object> getValues() {
        return values;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(" IN (");
        Iterator<Object> it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            oql.append(convertToOQL(value));
            if (it.hasNext()) {
                oql.append(", ");
            }
        }
        oql.append(')');

        return oql;
    }
}
