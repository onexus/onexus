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
import org.junit.Before;
import org.junit.Test;
import org.onexus.collection.api.*;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchCollectionStore;
import org.onexus.collection.store.elasticsearch.internal.HashEntity;
import org.onexus.collection.store.elasticsearch.mocks.MockResourceManager;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElasticSearchTest extends AbstractCollectionTest {

    private static final String PROJECT_URL = "http://test.onexus.org";

    private Collection collection01;

    private Collection pathways;
    private Collection projects;
    private Collection expression;

    private ElasticSearchCollectionStore store;

    public ElasticSearchTest() {
        super(ElasticSearchTest.class, PROJECT_URL);
    }

    @Before
    public void start() {

        // Create resource manager and collection store
        IResourceManager resourceManager = new MockResourceManager();
        store = new ElasticSearchCollectionStore();
        store.setResourceManager(resourceManager);
        store.start();

        // Save project
        resourceManager.save(new Project(PROJECT_URL, "test.onexus.org"));

        // Load definitions
        collection01 = getCollection01();
        pathways = getCollection("/data/pathways.onx");
        projects = getCollection("/data/projects.onx");
        expression = getCollection("/data/pathway-expression.onx");

        // Save to resource manager
        resourceManager.save(collection01);
        resourceManager.save(pathways);
        resourceManager.save(projects);
        resourceManager.save(expression);

        // Register collections
        forceRegister(collection01);
        forceRegister(pathways);
        forceRegister(projects);
        forceRegister(expression);

    }

    private void forceRegister(Collection collection) {
        ORI ori = collection.getORI();
        if (store.isRegistered(ori)) {
            store.deregister(ori);
        }
        store.register(ori);
    }

    @Test
    public void insert01() {

        IEntity entity = getEntity(collection01);

        store.insert(entity);

        assertTrue(store.isRegistered(collection01.getORI()));

        Query query = new Query();
        query.addDefine("e", collection01.getORI());
        query.setFrom("e");

        IEntityTable result = store.load(query);

        assertTrue(result.next());
        assertEquals(1, result.size());

        IEntity e = result.getEntity(collection01.getORI());

        assertEquals("testid", e.get("id"));
        assertEquals("testvalue", e.get("value"));

    }

    @Test
    public void insert02() {
        insertCollectionAndAssertSize(projects, 310);
        insertCollectionAndAssertSize(pathways, 260);
        insertCollectionAndAssertSize(expression, 19986);
    }

    private void insertCollectionAndAssertSize(Collection collection, int size) {
        long start = System.currentTimeMillis();

        IEntitySet entity = readCollection(collection);

        store.insert(entity);

        assertTrue(store.isRegistered(collection.getORI()));

        Query query = new Query();
        query.addDefine("e", collection.getORI());
        query.setFrom("e");

        IEntityTable result = store.load(query);

        assertTrue(collection.toString(), result.next());
        assertEquals(collection.toString(), size, result.size());

        System.out.println(collection.getORI().toString() + " in " + (System.currentTimeMillis() - start) + " ms");
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

    private Collection getCollection01() {
        Collection collection = new Collection();
        collection.setORI(new ORI(PROJECT_URL, "collection"));
        collection.setFields(Arrays.asList(
                new Field("id", "identifier", null, String.class, true),
                new Field("value", "value", null, String.class)
        ));
        return collection;
    }

}
