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
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ElasticSearchEntityTable implements IEntityTable {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchEntityTable.class);
    public static final HashMap<String, Object> EMPTY_VALUES = new HashMap<String, Object>(0);
    private IResourceManager resourceManager;
    private ElasticSearchQuery query;

    private long size = -1;
    private Iterator<SearchHit> hits;

    private SearchHit currentHit;
    private Map<ORI, IEntity> currentHitEntityCache;

    public ElasticSearchEntityTable(IResourceManager resourceManager, LoadingCache<ORI, String> indexNameCache, Client client, Query query) {
        super();

        this.resourceManager = resourceManager;
        try {
            this.query = new ElasticSearchQuery(resourceManager, indexNameCache, client, query);
        } catch (IndexMissingException e) {
            this.hits = Collections.EMPTY_LIST.iterator();
            this.size = 0;
        }

        this.currentHitEntityCache = new HashMap<ORI, IEntity>();
    }

    @Override
    public Query getQuery() {
        return query.getOnexusQuery();
    }

    @Override
    public IEntity getEntity(ORI collectionOri) {

        if (collectionOri == null) {
            return null;
        }

        collectionOri = collectionOri.toAbsolute(query.getOnexusQuery().getOn());

        if (currentHitEntityCache.containsKey(collectionOri)) {
            return currentHitEntityCache.get(collectionOri);
        }

        Collection collection = resourceManager.load(Collection.class, collectionOri);

        if (!query.isLinked(collectionOri)) {
            return new HashEntity(collection, EMPTY_VALUES);
        }

        Map<String, Object> values = currentHit.sourceAsMap();
        List<String> prefixes = query.getPath(collectionOri);
        for (String prefix : prefixes) {

            values = (Map<String, Object>) values.get(prefix);

            if (values == null) {
                values = EMPTY_VALUES;
            }

        }

        if (values == null) {
            values = EMPTY_VALUES;
        }

        IEntity entity = new HashEntity(collection, values);
        this.currentHitEntityCache.put(collectionOri, entity);

        return entity;
    }

    @Override
    public boolean next() {

        if (hits == null) {
            init();
        }

        if (!hits.hasNext()) {
            currentHit = null;
            return false;
        }

        currentHit = hits.next();
        currentHitEntityCache.clear();
        
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public long size() {

        if (hits == null) {
            init();
        }

        return size;
    }

    private void init() {

        SearchRequestBuilder searchRequest = this.query.getSearchRequest();

        if (log.isDebugEnabled()) {
            log.debug(searchRequest.toString());
        }

        SearchHits hits = searchRequest.execute().actionGet().getHits();

        this.size = hits.totalHits();
        this.hits = hits.iterator();

    }

    @Override
    public Progress getProgress() {
        return null;
    }
}
