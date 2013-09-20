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
package org.onexus.collection.manager.internal;

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Progress;

public class ProgressEntityTable implements IEntityTable {

    private Progress progress;

    private IEntityTable entityTable;

    public ProgressEntityTable(Progress progress, IEntityTable entityTable) {
        this.progress = progress;
        this.entityTable = entityTable;
    }


    @Override
    public Query getQuery() {
        return entityTable.getQuery();
    }

    @Override
    public IEntity getEntity(ORI collectionURI) {
        return entityTable.getEntity(collectionURI);
    }

    @Override
    public boolean next() {
        return entityTable.next();
    }

    @Override
    public void close() {
        entityTable.close();
    }

    @Override
    public long size() {
        return entityTable.size();
    }

    @Override
    public Progress getProgress() {
        return progress;
    }
}
