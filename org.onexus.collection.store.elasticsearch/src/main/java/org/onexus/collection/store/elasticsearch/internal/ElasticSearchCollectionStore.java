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
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.onexus.collection.api.*;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.SingleEntityEntitySet;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.*;

public class ElasticSearchCollectionStore implements ICollectionStore {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchCollectionStore.class);

    private static final int INSERT_BULK_SIZE = 1000;
    private final BulkListener BULK_LISTENER = new BulkListener();

    private Node node;
    private Client client;
    private LoadingCache<ORI, String> indexNameCache;
    private IResourceManager resourceManager;

    public static final String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    public static final String ES_DEFAULT_FOLDER = ONEXUS_FOLDER + File.separator + "elasticsearch";
    private String homePath;

    private static MBeanServerConnection TO_FORCE_IMPORT;

    public ElasticSearchCollectionStore() {
        super();
    }

    public ElasticSearchCollectionStore(String homePath){
        super();
        this.homePath = homePath;
    }

    public void start() {

        // Create and start a node
        node = buildNode(homePath == null ? ES_DEFAULT_FOLDER : homePath);
        node.start();

        // Create a client
        client = node.client();

        // Create ORI to index name cache
        indexNameCache = newOriToIndexNameCache(resourceManager, 100);
    }

    public void stop(ICollectionStore store, @SuppressWarnings("rawtypes") Map properties) {

        // Stop the client and the node
        client.close();
        node.close();

    }

    @Override
    public boolean isRegistered(ORI collectionOri) {

        String indexName = indexNameCache.getUnchecked(collectionOri);
        boolean result = exists(client, indexName);

        // If exists then double check after a flush
        if (result) {
            refreshIndex(client, indexName);
            result = exists(client, indexName);
        }

        return result;
    }

    @Override
    public void register(ORI collectionOri) {

        if (isRegistered( collectionOri )) {
            throw new UnsupportedOperationException("The collection '" + collectionOri + "' is already registered.");
        }

        Collection collection = resourceManager.load(Collection.class, collectionOri);

        if (collection == null) {
            throw new UnsupportedOperationException("The resource '" + collectionOri + "' don't exists or it's not a collection.");
        }

        // Prepare embedded links
        createIndex(client, indexNameCache.getUnchecked(collectionOri), collection, createEmbeddedLinks(collection));

    }

    private List<EmbeddedLink> createEmbeddedLinks(Collection collection) {

        List<Link> links = (collection.getLinks() == null ? Collections.EMPTY_LIST : collection.getLinks());
        List<EmbeddedLink> embeddedLinks = new ArrayList<EmbeddedLink>(links.size());
        for (Link link : links) {

            ORI ori = link.getCollection().toAbsolute(collection.getORI());
            Collection toCollection = resourceManager.load(Collection.class, ori);
            List<EmbeddedLink> toLinks = createEmbeddedLinks(toCollection);

            embeddedLinks.add(new EmbeddedLink(indexNameCache.getUnchecked(ori), link.getFields(), toCollection, toLinks));
        }
        return embeddedLinks;
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

        Collection collection = dataSet.getCollection();

        if (!isRegistered( collection.getORI() )) {
            throw new UnsupportedOperationException("The collection '" + collection.getORI() + "' is not registered.");
        }

        try {

            String mainIndex = indexNameCache.getUnchecked(dataSet.getCollection().getORI());

            refreshIndex(client, mainIndex);

            // Prepare embedded links
            List<Link> links = (collection.getLinks() == null ? Collections.EMPTY_LIST : collection.getLinks());
            List<EmbeddedLink> embeddedLinks = new ArrayList<EmbeddedLink>(links.size());
            for (Link link : links) {
                ORI ori = link.getCollection().toAbsolute(collection.getORI());
                String indexName = indexNameCache.getUnchecked(ori);
                refreshIndex(client, indexName);
                embeddedLinks.add(new EmbeddedLink( indexName, link.getFields()));
            }

            int bulkSize = 0;
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            while (dataSet.next()) {

                if (bulkSize == INSERT_BULK_SIZE) {
                    bulkRequest.execute(BULK_LISTENER);
                    bulkRequest = client.prepareBulk();
                    bulkSize=0;
                }

                bulkRequest.add(insertEntity(client, mainIndex, dataSet, embeddedLinks));
                bulkSize++;

            }

            if (bulkRequest.numberOfActions() > 0) {
                logErrors( bulkRequest.execute().actionGet() );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public IEntityTable load(Query query) {
        return new ElasticSearchEntityTable(resourceManager, indexNameCache, client, query);
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

    private class BulkListener implements ActionListener<BulkResponse> {

        @Override
        public void onResponse(BulkResponse bulkItemResponses) {
            logErrors(bulkItemResponses);
        }

        @Override
        public void onFailure(Throwable e) {
            log.error(e.getMessage());
        }
    }

    private static void logErrors(BulkResponse bulkItemResponses) {
        if (bulkItemResponses.hasFailures()) {
            for (BulkItemResponse response : bulkItemResponses) {
                log.error("Error inserting. " + response.getFailureMessage());
            }
        }
    }

}
