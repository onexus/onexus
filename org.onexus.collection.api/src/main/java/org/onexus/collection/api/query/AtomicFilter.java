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

public abstract class AtomicFilter extends Filter {

    private String collectionAlias;

    private String fieldId;

    private Object value;

    public AtomicFilter() {
        super();
    }

    public AtomicFilter(String collectionAlias, String fieldId, Object value) {
        this.collectionAlias = collectionAlias;
        this.fieldId = fieldId;
        this.value = value;
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public abstract String getOperandSymbol();

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        oql.append(collectionAlias);
        oql.append('.');
        oql.append(fieldId);
        oql.append(' ').append(getOperandSymbol()).append(' ');
        oql.append(Filter.convertToOQL(value));

        return oql;
    }
}
