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
import org.onexus.resource.api.ORI;

import java.util.List;

/**
 * <p>A ICollectionStore persist a {@link org.onexus.collection.api.Collection} and runs a {@link org.onexus.collection.api.query.Query} on it.</p>
 *
 * @author Jordi Deu-Pons
 */
public interface ICollectionStore {

    /**
     * Check if a collection is registered in this store.
     *
     * @param collectionOri The Collection URI.
     * @return Returns true if it's stored and false if not.
     */
    boolean isRegistered(ORI collectionOri);

    /**
     * Prepare the store to be able to insert entities of this collection.
     *
     * @param collectionOri The Collection URI
     */
    void register(ORI collectionOri);

    /**
     * Remove the collection from the store.
     *
     * @param collectionOri
     */
    void deregister(ORI collectionOri);


    /**
     * @return A list with all the registered collections URIs
     */
    List<String> getRegistered();

    /**
     * Insert one entity.
     *
     * @param entity The entity to be inserted
     */
    void insert(IEntity entity);

    /**
     * Insert a entity set.
     *
     * @param dataSet The entity set to be inserted
     */
    void insert(IEntitySet dataSet);

    /**
     * Query the store
     *
     * @param query The {@link Query} of interest
     * @return The result {@link IEntityTable} for the query.
     */
    IEntityTable load(Query query);

}
