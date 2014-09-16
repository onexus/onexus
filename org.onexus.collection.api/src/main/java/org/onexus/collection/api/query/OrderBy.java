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

import org.onexus.resource.api.Resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * This defines a sort direction in a OQL query.
 */
public class OrderBy implements Serializable {

    /**
     * The collection to sort.
     */
    @NotNull
    private String collection;

    /**
     * The field to sort.
     */
    @NotNull
    @Pattern(regexp = Resource.PATTERN_ID)
    private String field;

    private boolean ascendent = true;

    /**
     * Keep this constructor for JAXB compatibility.
     */
    @SuppressWarnings("UnusedDeclaration")
    public OrderBy() {
        super();
    }

    /**
     * Create a <code>OrderBy</code>.
     *
     * @param collection The collection alias to order.
     * @param field      The field id to order.
     */
    public OrderBy(final String collection, final String field) {
        this(collection, field, true);
    }

    /**
     * Create a <code>OrderBy</code>.
     *
     * @param collection The collection alias to order.
     * @param field      The field id to order.
     * @param ascendent  The sorting direction.
     */
    public OrderBy(final String collection, final String field, final boolean ascendent) {
        this.collection = collection;
        this.field = field;
        this.ascendent = ascendent;
    }

    /**
     * The sorted collection
     *
     * @return The collection alias.
     */
    public final String getCollection() {
        return collection;
    }

    /**
     * Sets the sorted collection.
     *
     * @param collection The collection alias.
     */
    public final void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * The sorted field.
     *
     * @return The field id.
     */
    public final String getField() {
        return field;
    }

    /**
     * Sets the sorted field.
     *
     * @param field The field id to order.
     */
    public void setField(final String field) {
        this.field = field;
    }

    /**
     * Sorting direction.
     *
     * @return True if it's ascending, false descending.
     */
    public final boolean isAscendent() {
        return ascendent;
    }

    /**
     * Sets the sorting direction.
     *
     * @param ascendent True if it's ascending, false descending.
     */
    public void setAscendent(final boolean ascendent) {
        this.ascendent = ascendent;
    }

    @Override
    public final String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    /**
     * Returns the input 'oql' StringBuilder after append this filter OQL string.
     *
     * @param oql         A StringBuilder to append the OQL
     * @param prettyPrint If true then add tabs and new line characters to format the OQL query.
     * @return The OQL query
     */
    public final StringBuilder toString(final StringBuilder oql, final boolean prettyPrint) {
        oql.append(collection).append('.').append(field);
        if (!ascendent) {
            oql.append(" DESC");
        }
        return oql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderBy orderBy = (OrderBy) o;

        if (ascendent != orderBy.ascendent) {
            return false;
        }
        if (collection != null ? !collection.equals(orderBy.collection) : orderBy.collection != null) {
            return false;
        }

        if (field != null ? !field.equals(orderBy.field) : orderBy.field != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = collection != null ? collection.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (ascendent ? 1 : 0);
        return result;
    }
}
