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

import java.io.Serializable;

public class OrderBy implements Serializable {

    private String collection;

    private String field;

    private boolean ascendent = true;

    public OrderBy() {
    }

    public OrderBy(String collection, String field) {
        this(collection, field, true);
    }

    public OrderBy(String collection, String field, boolean ascendent) {
        this.collection = collection;
        this.field = field;
        this.ascendent = ascendent;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAscendent() {
        return ascendent;
    }

    public void setAscendent(boolean ascendent) {
        this.ascendent = ascendent;
    }

    public String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {
        oql.append(collection).append('.').append(field);
        if (!ascendent) {
            oql.append(" DESC");
        }
        return oql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBy orderBy = (OrderBy) o;

        if (ascendent != orderBy.ascendent) return false;
        if (collection != null ? !collection.equals(orderBy.collection) : orderBy.collection != null)
            return false;
        if (field != null ? !field.equals(orderBy.field) : orderBy.field != null) return false;

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
