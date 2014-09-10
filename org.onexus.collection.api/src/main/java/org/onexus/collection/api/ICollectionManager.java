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
package org.onexus.collection.api;

import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceService;
import org.onexus.resource.api.ORI;

/**
 * <p>A ICollectionManager manage the life-cycle of a collection. We can have a non-blocking
 * ICollectionManager, so it will return partial {@link IEntityTable} result with a {@link org.onexus.resource.api.Progress}
 * in process. It is the user that have to keep calling getTaskStatus() method until the task
 * is done and then call again the load() method with the same query to get all the results.</p>
 */
public interface ICollectionManager extends IResourceService {

    /**
     * @param query The query
     * @return The result of the query. If it's not a blocking ICollectionManager
     * it can return a partial result but with a {@link org.onexus.resource.api.Progress} under process.
     */
    IEntityTable load(Query query);

    /**
     * Unload the collection from origin store.
     *
     * @param collectionOri
     */
    void unload(ORI collectionOri);

    boolean isLinkable(Query query, ORI collectionOri);
}
