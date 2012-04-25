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
package org.onexus.core;

import org.onexus.core.query.Query;

import java.util.List;

/**
 * <p>A ICollectionStore persist a {@link org.onexus.core.resources.Collection} and runs a {@link org.onexus.core.query.Query} on it.</p>
 *
 * @author Jordi Deu-Pons
 */
public interface ICollectionStore {

    public String STATUS_ENABLED = "enabled";
    public String STATUS_DISABLED = "disabled";

    /**
     * @return This collection store is status ('enabled' or 'disabled').
     */
    public String getStatus();

    /**
     * Check if a collection is registered in this store.
     *
     * @param collectionURI The Collection URI.
     * @return Returns true if it's stored and false if not.
     */
    public boolean isRegistered(String collectionURI);

    /**
     * Prepare the store to be able to insert entities of this collection.
     *
     * @param collectionURI The Collection URI
     */
    public void registerCollection(String collectionURI);

    /**
     * Remove the collection from the store.
     *
     * @param collectionURI
     */
    public void unregisterCollection(String collectionURI);


    /**
     * @return A list with all the registered collections URIs
     */
    public List<String> getRegisteredCollections();

    /**
     * Insert one entity.
     *
     * @param entity The entity to be inserted
     */
    public void insert(IEntity entity);

    /**
     * Insert a entity set.
     *
     * @param dataSet The entity set to be inserted
     */
    public void insert(IEntitySet dataSet);

    /**
     * Query the store
     *
     * @param query The {@link Query} of interest
     * @return The result {@link IEntityTable} for the query.
     */
    public IEntityTable load(Query query);

}
