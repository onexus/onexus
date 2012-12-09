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

import java.util.Collections;
import java.util.Iterator;

public class EmptyEntitySet implements IEntitySet {

    private Collection collection;

    public EmptyEntitySet(Collection collection) {
        this.collection = collection;
    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    public IEntity detachedEntity() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public Object get(String fieldId) {
        return null;
    }

    @Override
    public void put(String fieldName, Object value) {
    }

    @Override
    public Iterator<IEntity> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
}
