/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.core.utils;

import java.util.Iterator;

import org.onexus.core.IEntity;
import org.onexus.core.IEntitySet;
import org.onexus.core.IEntityTable;

public class EntityIterator implements Iterator<IEntity> {

    private transient IEntitySet entitySet;

    private transient String collectionURI;
    private transient IEntityTable entityTable;

    private boolean _movedToNext = false;
    private boolean _lastNext = true;

    public EntityIterator(IEntitySet entitySet) {
	super();

	this.entitySet = entitySet;
	this.collectionURI = null;
	this.entityTable = null;
    }

    public EntityIterator(IEntityTable entityTable, String collectionURI) {
	super();

	this.entitySet = null;
	this.collectionURI = collectionURI;
	this.entityTable = entityTable;
    }

    @Override
    public boolean hasNext() {
	if (_lastNext == false) {
	    return false;
	}

	if (_movedToNext == true) {
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
	    return entityTable.getEntity(collectionURI);
	}
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException("Read-only iterator");
    }

}
