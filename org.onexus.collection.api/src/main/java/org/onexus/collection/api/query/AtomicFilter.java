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
 * AtomicFilter serves as the highest level abstract base class for all OQL
 * filters that are atomic and use only one field of a collection.
 */
public abstract class AtomicFilter extends Filter {

    /** The collection to filter. **/
    private String collectionAlias;

    /** The field to filter. **/
    private String fieldId;

    /** The value to compare and filter. **/
    private Object value;

    /**
     * Keep this constructor for JAXB compatibility.
     */
    public AtomicFilter() {
        super();
    }

    /**
     * New instance of an atomic filter.
     *
     * @param collectionAlias The collection alias that filters this filter.
     * @param fieldId         The filter id that filters this filter.
     * @param value           The value that uses this filter to filter the
     *                        collection.
     */
    public AtomicFilter(final String collectionAlias, final String fieldId, final Object value) {
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
        this.value = value;
    }

    /**
     * The collection alias that filters this <code>AtomicFilter</code>.
     *
     * @return The collection alias.
     */
    public final String getCollectionAlias() {
        return collectionAlias;
    }

    /**
     * Sets the collection alias that filters this filter.
     *
     * @param collectionAlias The collection to filter.
     */
    public final void setCollectionAlias(final String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    /**
     * The field id that filters this filter.
     *
     * @return Thie field id.
     */
    public final String getFieldId() {
        return fieldId;
    }

    /**
     * Sets the field id that filters this filter.
     *
     * @param fieldId The field id to filter.
     */
    public final void setFieldId(final String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * The value that uses this filter to filter the collection.
     *
     * @return The filter value.
     */
    public final Object getValue() {
        return value;
    }

    /**
     * Sets the filter value to use when filtering the collection.
     *
     * @param value The filter value
     */
    public final void setValue(final Object value) {
        this.value = value;
    }

    /**
     * @return the OQL string representation of this filter operand.
     */
    public abstract String getOperandSymbol();

    @Override
    public final StringBuilder toString(final StringBuilder oql, final boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(' ').append(getOperandSymbol()).append(' ');
        oql.append(Filter.convertToOQL(value));

        return oql;
    }
}
