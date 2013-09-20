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

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.api.IEntityTable;
import org.onexus.resource.api.ORI;

import java.util.Iterator;

public class EntityIterator implements Iterator<IEntity> {

    private transient IEntitySet entitySet;

    private transient ORI collectionOri;
    private transient IEntityTable entityTable;

    private boolean _movedToNext = false;
    private boolean _lastNext = true;

    public EntityIterator(IEntitySet entitySet) {
        super();

        this.entitySet = entitySet;
        this.collectionOri = null;
        this.entityTable = null;
    }

    public EntityIterator(IEntityTable entityTable, ORI collectionOri) {
        super();

        this.entitySet = null;
        this.collectionOri = collectionOri;
        this.entityTable = entityTable;
    }

    @Override
    public boolean hasNext() {
        if (!_lastNext) {
            return false;
        }

        if (_movedToNext) {
            return true;
        }

        _movedToNext = true;
        _lastNext = internalNext();

        return _lastNext;
    }

    private boolean internalNext() {
        if (entitySet != null) {
            return entitySet.next();
        }
        return entityTable.next();
    }

    @Override
    public IEntity next() {

        if (!_movedToNext) {
            _lastNext = internalNext();
        }

        _movedToNext = false;

        if (entitySet != null) {
            return entitySet.detachedEntity();
        } else {
            return entityTable.getEntity(collectionOri);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Read-only iterator");
    }

}
