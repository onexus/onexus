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
 * This filter select a single entity of a collection using it's primary key.
 */
public class EqualId extends Filter {

    /** The collection alias to filter. **/
    private String collectionAlias;

    /** The primary key of the entity to select. **/
    private Object id;

    /**
     * Keep this constructor for JAXB compatibility.
     */
    @SuppressWarnings("UnusedDeclaration")
    public EqualId() {
        super();
    }

    /**
     * Creates EQUAL ID filter.
     *
     * @param collectionAlias The collection alias to filter.
     * @param id              The primary key of the entity to select.
     */
    public EqualId(final String collectionAlias, final Object id) {
        this.collectionAlias = collectionAlias;
        this.id = id;
    }

    /**
     * The filtering collection.
     *
     * @return The alias of the collection.
     */
    public final String getCollectionAlias() {
        return collectionAlias;
    }

    /**
     * Sets the collection to filter.
     *
     * @param collectionAlias The alias of the collection to filter.
     */
    public final void setCollectionAlias(final String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    /**
     * The primary key of the entity to select.
     *
     * @return The <code>String</code> representation of the primary key.
     */
    public final Object getId() {
        return id;
    }

    /**
     * Sets the value of the primary key to select.
     *
     * @param id The primary key value to select.
     */
    public final void setId(final Object id) {
        this.id = id;
    }

    @Override
    public final StringBuilder toString(final StringBuilder oql, final boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append(" = ");
        oql.append(Filter.convertToOQL(id));

        return oql;
    }
}
