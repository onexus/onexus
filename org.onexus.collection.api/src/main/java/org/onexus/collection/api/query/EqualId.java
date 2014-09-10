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

    private String collectionAlias;

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
    public EqualId(String collectionAlias, Object id) {
        this.collectionAlias = collectionAlias;
        this.id = id;
    }

    /**
     * The filtering collection.
     *
     * @return The alias of the collection.
     */
    public String getCollectionAlias() {
        return collectionAlias;
    }

    /**
     * Sets the collection to filter.
     *
     * @param collectionAlias The alias of the collection to filter.
     */
    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    /**
     * The primary key of the entity to select.
     *
     * @return The <code>String</code> representation of the primary key.
     */
    public Object getId() {
        return id;
    }

    /**
     * Sets the value of the primary key to select.
     *
     * @param id The primary key value to select.
     */
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append(" = ");
        oql.append(Filter.convertToOQL(id));

        return oql;
    }
}
