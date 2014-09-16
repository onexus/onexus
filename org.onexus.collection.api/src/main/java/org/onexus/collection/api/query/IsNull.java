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
 * This filter, filters out the collection 'collectionAlias' entities that the 'fieldId' is
 * not null.
 */
public class IsNull extends Filter {

    /** The collection to filter. **/
    private String collectionAlias;

    /** The field id to filter. **/
    private String fieldId;

    /**
     * Keep this constructor for JAXB compatibility.
     */
    @SuppressWarnings("UnusedDeclaration")
    public IsNull() {
        super();
    }

    /**
     * Create a IS NULL filter.
     *
     * @param collectionAlias The collection alias to filter.
     * @param fieldId         The field id to filter.
     */
    public IsNull(final String collectionAlias, final String fieldId) {
        super();
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
    }

    /**
     * The filtering collection.
     *
     * @return The collection alias.
     */
    public final String getCollectionAlias() {
        return collectionAlias;
    }

    /**
     * Sets the collection to filter.
     *
     * @param collectionAlias The collection alias to filter.
     */
    public final void setCollectionAlias(final String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    /**
     * Gets the filtering field.
     *
     * @return The field id.
     */
    public final String getFieldId() {
        return fieldId;
    }

    /**
     * Sets the filtering field.
     *
     * @param fieldId The field id to filter.
     */
    public final void setFieldId(final String fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public final StringBuilder toString(final StringBuilder oql, final boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(" IS NULL");

        return oql;
    }
}
