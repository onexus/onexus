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
package org.onexus.collection.store.elasticsearch.internal;

import com.google.common.cache.LoadingCache;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.onexus.collection.api.*;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.SingleEntityEntitySet;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import java.io.IOException;
import java.util.*;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.*;

public class ElasticSearchCollectionStore implements ICollectionStore {

    private Node node;
    private Client client;
    private LoadingCache<ORI, String> indexNameCache;
    private IResourceManager resourceManager;

    public void start() {

        // Create and start a node
        node = buildNode();
        node.start();

        // Create a client
        client = node.client();

        // Create ORI to index name cache
        indexNameCache = newOriToIndexNameCache(100);
    }

    public void stop(ICollectionStore store, @SuppressWarnings("rawtypes") Map properties) {

        // Stop the client and the node
        client.close();
        node.close();

    }

    @Override
    public boolean isRegistered(ORI collectionOri) {
        return exists(client, indexNameCache.getUnchecked(collectionOri));
    }

    @Override
    public void register(ORI collectionOri) {

        if (isRegistered( collectionOri )) {
            throw new UnsupportedOperationException("The collection '" + collectionOri + "' is already registered.");
        }

    }

    @Override
    public void deregister(ORI collectionOri) {

        if (!isRegistered( collectionOri )) {
            throw new UnsupportedOperationException("The collection '" + collectionOri + "' is not registered.");
        }

        drop(client, indexNameCache.getUnchecked(collectionOri));
    }

    @Override
    public void insert(IEntity entity) {
        insert(new SingleEntityEntitySet(entity));
    }

    @Override
    public void insert(IEntitySet dataSet) {
        try {
            Collection collection = dataSet.getCollection();
            String mainIndex = indexNameCache.getUnchecked(dataSet.getCollection().getORI());

            // Prepare embedded links
            List<Link> links = (collection.getLinks() == null ? Collections.EMPTY_LIST : collection.getLinks());
            List<EmbeddedLink> embeddedLinks = new ArrayList<EmbeddedLink>(links.size());
            for (Link link : links) {
                ORI ori = link.getCollection().toAbsolute(collection.getORI());
                embeddedLinks.add(new EmbeddedLink( indexNameCache.getUnchecked(ori), link.getFields()));
            }

            while (dataSet.next()) {
                insertEntity(client, mainIndex, dataSet, embeddedLinks);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IEntityTable load(Query query) {
        return new ElasticSearchEntityTable(resourceManager, client, query);
    }

    @Override
    public List<String> getRegistered() {
        //TODO
        return null;
    }

    public Client getClient() {
        return client;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

}
