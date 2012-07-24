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
package org.onexus.collection.loader.tsv.internal;

import org.onexus.data.api.IDataManager;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.api.Collection;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Task;
import org.onexus.resource.api.utils.ResourceUtils;

import java.util.concurrent.Callable;

public class TsvCallable implements Callable<IEntitySet> {

    private final static String PARAMETER_DATA_URI = "data";

    private IDataManager dataManager;
    private Task task;
    private Collection collection;

    public TsvCallable(Task task, IDataManager dataManager, Collection collection) {
        super();

        this.dataManager = dataManager;
        this.task = task;
        this.collection = collection;
    }

    @Override
    public IEntitySet call() throws Exception {
        try {

            String dataUri = collection.getLoader().getParameter(PARAMETER_DATA_URI);
            String absDataUri = ResourceUtils.getAbsoluteURI( ResourceUtils.getParentURI(collection.getURI()), dataUri);

            IDataStreams dataStreams = dataManager.load(absDataUri);

            if (dataStreams.getTask() != null) {
                Task subTask = dataStreams.getTask();
                task.addSubTask( subTask );

                // Wait sub task finish
                while (!subTask.isDone()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (subTask.isCanceled()) {
                    task.setCancelled(true);
                    task.setDone(true);
                } else {
                    dataStreams = dataManager.load(absDataUri);
                }
            }

            return new TsvEntitySet(dataStreams, collection);

        } finally {
            this.task.setDone(true);
        }
    }

}
