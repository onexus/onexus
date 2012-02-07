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
package org.onexus.core;

import org.onexus.core.query.Query;

/**
 * <p>A ICollectionManager manage the life-cycle of a collection. We can have a non-blocking
 * ICollectionManager, so it will return partial {@link IEntityTable} result with a {@link TaskStatus}
 * in process. It is the user that have to keep calling getTaskStatus() method until the task
 * is done and then call again the load() method with the same query to get all the results.</p>
 * <p/>
 * <p>This is the typical event sequence the first time that one collection is required:</p>
 * <ul>
 * <li>CM = ICollectionManager</li>
 * <li>CS = ICollectionStore</li>
 * <li>TM = ITaskManager</li>
 * <li>TE = ITaskExecutor</li>
 * <li>TC = ITaskCallable</li>
 * </ul>
 * <img src="doc-files/basic-collection-loading.svg" width="100%" />
 *
 * @author Jordi Deu-Pons
 */
public interface ICollectionManager {


    /**
     * @param taskId The identifier of the {@link TaskStatus} that we want to refresh.
     * @return The current status of the taskId task.
     */
    public TaskStatus getTaskStatus(String taskId);


    /**
     * @param query The query
     * @return The result of the query. If it's not a blocking ICollectionManager
     *         it can return a partial result but with a {@link TaskStatus} under process.
     */
    public IEntityTable load(Query query);


    /**
     * Starts a background synchronization of the CollectionStores and the ResourceManagers
     */
    public void sync();

    /**
     * Unload the collection from origin store.
     *
     * @param collectionURI
     */
    public void unload(String collectionURI);

}
