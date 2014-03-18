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
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.convertOriToIndexName;

public class ElasticSearchEntityTable implements IEntityTable {

    private IResourceManager resourceManager;
    private ElasticSearchQuery query;

    private long size = -1;
    private Iterator<SearchHit> hits;
    private SearchHit currentHit;

    public ElasticSearchEntityTable(IResourceManager resourceManager, Client client, Query query) {
        super();

        this.resourceManager = resourceManager;

        try {
            this.query = new ElasticSearchQuery(resourceManager, client, query);
        } catch (IndexMissingException e) {
            this.hits = Collections.EMPTY_LIST.iterator();
            this.size = 0;
        }
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

        Collection collection = resourceManager.load(Collection.class, collectionOri);

        if (collectionOri.equals( query.getFrom() )) {
            return new HashEntity(collection, currentHit.sourceAsMap());
        }

        Map<String, Object> values = (Map<String, Object>) currentHit.sourceAsMap().get(convertOriToIndexName(collectionOri));

        if (values == null) {
            values = new HashMap<String, Object>(0);
        }

        return new HashEntity(collection, values);
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

        SearchHits hits = this.query.getSearchRequest().execute().actionGet().getHits();
        this.size = hits.totalHits();
        this.hits = hits.iterator();
    }

    @Override
    public Progress getProgress() {
        return null;
    }
}
