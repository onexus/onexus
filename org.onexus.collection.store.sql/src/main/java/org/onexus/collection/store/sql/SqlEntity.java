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
package org.onexus.collection.store.sql;

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;

import java.util.*;

public class SqlEntity implements IEntity {

    private Collection collection;
    private Map<String, Object> values;

    public SqlEntity(Collection collection) {
        this(collection, new HashMap<String, Object>());
    }

    private SqlEntity(Collection collection, Map<String, Object> values) {
        super();
        this.collection = collection;
        this.values = values;
    }

    @Override
    public String getId() {

        List<String> collectionKeys = new ArrayList<String>();
        for (Field field : collection.getFields()) {
            if (field.isPrimaryKey()!=null && field.isPrimaryKey()) {
                collectionKeys.add(field.getId());
            }
        }

        StringBuilder id = new StringBuilder();
        Iterator<String> fieldNames = collectionKeys.iterator();
        while (fieldNames.hasNext()) {
            id.append(String.valueOf(get(fieldNames.next())));
            if (fieldNames.hasNext()) {
                id.append("\t");
            }
        }

        return id.toString();
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public Object get(String fieldId) {
        return values.get(fieldId);
    }

    @Override
    public void put(String fieldURI, Object value) {
        values.put(fieldURI, value);
    }

    protected SqlEntity clone() {
        return new SqlEntity(collection, new HashMap<String, Object>(
                this.values));
    }

    @Override
    public String toString() {
        return "{" +
                "collection=" + collection +
                ", values=" + values +
                '}';
    }
}
