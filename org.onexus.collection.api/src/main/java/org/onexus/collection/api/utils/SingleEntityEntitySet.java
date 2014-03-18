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
package org.onexus.collection.api.utils;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntitySet;

import java.util.Arrays;
import java.util.Iterator;

public class SingleEntityEntitySet implements IEntitySet {

    private IEntity entity;
    private boolean next = true;

    public SingleEntityEntitySet(IEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean next() {

        if (next) {
            next = false;
            return true;
        }

        return false;
    }

    @Override
    public long size() {
        return 1;
    }

    @Override
    public void close() {
        entity = null;
        next = false;
    }

    @Override
    public IEntity detachedEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public Collection getCollection() {
        return entity.getCollection();
    }

    @Override
    public Object get(String fieldId) {
        return entity.get(fieldId);
    }

    @Override
    public void put(String fieldName, Object value) {
        entity.put(fieldName, value);
    }

    @Override
    public Iterator<IEntity> iterator() {
        return Arrays.asList(entity).iterator();
    }
}
