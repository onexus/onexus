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
package org.onexus.collection.manager.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.*;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.data.api.Progress;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CollectionManager implements ICollectionManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(CollectionManager.class);

    private IResourceManager resourceManager;

    private ICollectionStore collectionStore;

    private int maxThreads = 4;

    private ExecutorService executorService;
    private Map<String, Progress> runningTasks;
    private Map<String, Progress> runningCollections;

    public CollectionManager() {
        super();
        this.runningTasks = Collections.synchronizedMap(new HashMap<String, Progress>());
        this.runningCollections = Collections.synchronizedMap(new HashMap<String, Progress>());
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void init() {
        /* TODO
        if (executorService instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executorService).setMaximumPoolSize(maxThreads);
        }
        */
    }

    @Override
    public boolean isLinkable(Query query, String collectionUri) {

        Collection joinCollection = resourceManager.load(Collection.class, collectionUri);

        for (Map.Entry<String, String> tpDefine : query.getDefine().entrySet()) {

            String tpCollectionUri = QueryUtils.getAbsoluteCollectionUri(query, tpDefine.getValue());

            Collection tpJoinCollection = resourceManager.load(Collection.class, tpCollectionUri);

            List<FieldLink> links = LinkUtils.getLinkFields(query.getOn(), joinCollection, tpJoinCollection);

            if (links != null && !links.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IEntityTable load(Query query) {
        LOGGER.debug("Loading query {}", query);

        Set<String> notRegisteredCollections = new HashSet<String>();
        for (String collectionURI : getQueryCollections(query)) {
            if (!collectionStore.isRegistered(collectionURI)) {
                notRegisteredCollections.add(collectionURI);
            }
        }

        String taskId = Integer.toHexString(query.hashCode());
        Progress progress = getTask(taskId);

        if (progress == null && !notRegisteredCollections.isEmpty()) {
            progress = new Progress(taskId, "Loading collections");

            LOGGER.info("Starting task {}", taskId);
            for (String collectionURI : notRegisteredCollections) {

                Project project = resourceManager.load(Project.class, ResourceUtils.getProjectURI(collectionURI));
                Collection collection = resourceManager.load(Collection.class, collectionURI);

                if (collection == null) {
                    progress.error("Unknown collection '" + collectionURI + "'");
                    progress.fail();
                } else {

                    String subTaskId = Integer.toHexString(collectionURI.hashCode());

                    if (!runningCollections.containsKey(subTaskId)) {

                        Progress subProgress = new Progress(subTaskId, "Load '" + ResourceUtils.getResourcePath(collectionURI) + "'");
                        runningCollections.put(subTaskId, subProgress);

                        LOGGER.info("Registering {}", collectionURI);
                        collectionStore.register(collectionURI);

                        LOGGER.info("Submiting store collection '{}'", collectionURI);

                        Loader loader = collection.getLoader();
                        Plugin plugin = project.getPlugin(loader.getPlugin());
                        ICollectionLoader collectionLoader = resourceManager.getLoader(ICollectionLoader.class, plugin, loader);

                        Runnable command = new InsertCollectionRunnable(runningCollections, plugin, collection, collectionLoader, collectionStore);
                        executorService.submit(command);

                    }

                    progress.addSubTask(runningCollections.get(subTaskId));
                }

            }
        }

        IEntityTable partialResults = collectionStore.load(query);

        if (progress != null) {
            partialResults = new ProgressEntityTable(progress, partialResults);
        }

        return partialResults;
    }

    private Set<String> getQueryCollections(Query query) {

        Set<String> queryCollections = new HashSet<String>();
        String onUri = query.getOn();

        for (String collectionUri : query.getDefine().values()) {
            queryCollections.add(ResourceUtils.getAbsoluteURI(onUri, collectionUri));
        }

        return queryCollections;
    }

    public Progress getTask(String taskId) {
        Progress progress = runningTasks.get(taskId);

        if (progress == null) {
            return null;
        }

        if (progress.isDone()) {
            runningTasks.remove(taskId);
        }

        return progress;
    }

    @Override
    public void unload(String collectionURI) {

        if (collectionStore.isRegistered(collectionURI)) {
            // If in future exist multiple collectionStores this action makes
            // not sense!
            collectionStore.deregister(collectionURI);
        }
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public ICollectionStore getCollectionStore() {
        return collectionStore;
    }

    public void setCollectionStore(ICollectionStore collectionStore) {
        this.collectionStore = collectionStore;
    }

}
