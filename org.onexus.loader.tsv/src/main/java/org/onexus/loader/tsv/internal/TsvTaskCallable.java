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
package org.onexus.loader.tsv.internal;

import org.onexus.data.api.IDataManager;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.api.ITask;
import org.onexus.collection.api.TaskStatus;
import org.onexus.collection.api.Collection;

public class TsvTaskCallable implements ITask {

    private IDataManager dataManager;
    private TaskStatus status;
    private Collection collection;

    public TsvTaskCallable(IDataManager dataManager, Collection collection) {
        super();

        this.dataManager = dataManager;
        this.status = new TaskStatus(Long.toHexString(System.currentTimeMillis() + collection.hashCode()), "Loading TSV file '" + collection.getLoader().getParameter("FILE_URL") + "'");
        this.collection = collection;
    }

    @Override
    public IEntitySet call() throws Exception {
        status.addLog(status.getTitle());
        try {
            return new FileEntitySet(dataManager, collection);
        } finally {
            this.status.setDone(true);
        }
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

}
