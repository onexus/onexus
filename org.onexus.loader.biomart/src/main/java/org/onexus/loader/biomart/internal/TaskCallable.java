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
package org.onexus.loader.biomart.internal;

import org.onexus.core.IEntitySet;
import org.onexus.core.ITask;
import org.onexus.core.TaskStatus;

public class TaskCallable implements ITask {

    private TaskStatus status;
    private BiomartRequest request;

    public TaskCallable(BiomartRequest request) {
        this.request = request;

        String collectionURI = request.getCollection().getURI();
        this.status = new TaskStatus(Long.toHexString(collectionURI.hashCode()), "Preparing BIOMART collection '" + collectionURI + "'");
    }

    @Override
    public IEntitySet call() throws Exception {
        this.status.addLog("Downloading and parsing the collection");
        return new BiomartEntitySet(status, request);
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

}
