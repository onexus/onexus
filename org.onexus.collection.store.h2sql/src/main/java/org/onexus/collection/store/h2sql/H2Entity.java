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
package org.onexus.collection.store.h2sql;

import org.onexus.core.IEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

import java.util.*;

public class H2Entity implements IEntity {

    private Collection collection;
    private Map<String, Object> values;

    public H2Entity(Collection collection) {
        this(collection, new HashMap<String, Object>());
    }

    private H2Entity(Collection collection, Map<String, Object> values) {
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
    public Object get(String fieldURI) {
        return values.get(fieldURI);
    }

    @Override
    public void put(String fieldURI, Object value) {
        values.put(fieldURI, value);
    }

    protected H2Entity clone() {
        return new H2Entity(collection, new HashMap<String, Object>(
                this.values));
    }

}
