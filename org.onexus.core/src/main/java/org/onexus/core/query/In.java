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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class In extends Filter {

    private String collectionAlias;

    private String fieldId;

    private Set<Object> values = new HashSet<Object>();

    public In() {
        super();
    }

    public In(String collectionAlias, String fieldId) {
        super();
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
    }

    public String getCollectionAlias() {
        return collectionAlias;
    }

    public void setCollectionAlias(String collectionAlias) {
        this.collectionAlias = collectionAlias;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

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
            oql.append( convertToOQL(value) );
            if (it.hasNext()) {
                oql.append(", ");
            }
        }
        oql.append(')');

        return oql;
    }
}
