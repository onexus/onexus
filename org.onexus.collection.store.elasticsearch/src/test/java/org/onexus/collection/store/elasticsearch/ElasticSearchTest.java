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
package org.onexus.collection.store.elasticsearch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchCollectionStore;
import org.onexus.collection.store.elasticsearch.internal.HashEntity;
import org.onexus.collection.store.elasticsearch.mocks.MockResourceManager;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElasticSearchTest {

    public static final ORI COLLECTION_ORI = new ORI("http://test?collection");

    private Collection collection;
    private ElasticSearchCollectionStore store;

    @Before
    public void start() {

        // Initialize resource manager
        IResourceManager resourceManager = new MockResourceManager();
        collection = getCollection();
        resourceManager.save(collection);

        // Initialize collection store
        store = new ElasticSearchCollectionStore();
        store.setResourceManager(resourceManager);
        store.start();
    }

    @Test
    public void insert() {

        if (store.isRegistered(collection.getORI())) {
            store.deregister(collection.getORI());
        }

        IEntity entity = getEntity(collection);

        store.insert(entity);

        assertTrue(store.isRegistered(collection.getORI()));

        Query query = new Query();
        query.addDefine("e", COLLECTION_ORI);
        query.setFrom("e");

        IEntityTable result = store.load(query);

        assertTrue(result.next());
        assertEquals(1, result.size());

        IEntity e = result.getEntity(COLLECTION_ORI);

        assertEquals("testid", e.get("id"));
        assertEquals("testvalue", e.get("value"));

    }

    @After
    public void stop() {
        store.stop(store, null);
    }

    private IEntity getEntity(Collection collection) {
        IEntity entity = new HashEntity(collection);
        entity.put("id", "testid");
        entity.put("value", "testvalue");
        return entity;
    }

    private Collection getCollection() {
        Collection collection = new Collection();
        collection.setORI(COLLECTION_ORI);
        collection.setFields(Arrays.asList(
                new Field("id", "identifier", null, String.class, true),
                new Field("value", "value", null, String.class)
        ));
        return collection;
    }

}
