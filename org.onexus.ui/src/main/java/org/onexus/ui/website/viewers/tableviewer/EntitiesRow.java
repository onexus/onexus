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
package org.onexus.ui.website.viewers.tableviewer;

import org.onexus.core.IEntityTable;

import java.io.Serializable;
import java.util.Iterator;

public class EntitiesRow implements Iterator<IEntityTable>, Serializable {

    private transient IEntityTable dataMatrix = null;
    private transient boolean hasNextCalled = false;
    private transient boolean hasNext;

    public EntitiesRow(IEntityTable dataMatrix) {
        this.dataMatrix = dataMatrix;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            hasNext = dataMatrix.next();
            hasNextCalled = true;
        }

        return hasNext;
    }

    @Override
    public IEntityTable next() {
        if (!hasNextCalled) {
            hasNext = dataMatrix.next();
        }
        hasNextCalled = false;
        return dataMatrix;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Read only iterator");
    }

}
