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
package org.onexus.collection.store.elasticsearch.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntity;

import java.util.HashMap;
import java.util.Map;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.buildKey;

public class HashEntity implements IEntity {

    private Collection collection;
    private Map<String, Object> values;

    public HashEntity(Collection collection) {
        this(collection, new HashMap<String, Object>());
    }

    public HashEntity(Collection collection, Map<String, Object> values) {
        this.collection = collection;
        this.values = values;
    }

    @Override
    public String getId() {
        return buildKey(this);
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
    public void put(String fieldName, Object value) {
        values.put(fieldName, value);
    }

}
