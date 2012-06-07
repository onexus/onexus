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
package org.onexus.core.query;

public class EqualId extends Filter {

    private String collectionAlias;

    private Object id;


    public EqualId() {
        super();
    }

    public EqualId(String collectionAlias, Object id) {
        this.collectionAlias = collectionAlias;
        this.id = id;
    }

    public String getCollectionAlias() {
        return collectionAlias;
    }

    public Object getId() {
        return id;
    }

    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

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
