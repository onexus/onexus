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
import org.onexus.collection.api.ICollectionLoader;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.ICollectionStore;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.resource.api.*;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.resource.api.utils.ResourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionManager implements ICollectionManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(CollectionManager.class);

    private IResourceManager resourceManager;

    private ICollectionStore collectionStore;

    private IProgressManager progressManager;

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

        resourceManager.addResourceListener(new ResourceListener() {
            @Override
            public void onProjectDelete(Project project) {

                LoginContext.set(LoginContext.SERVICE_CONTEXT, null);

                // Unload all the collections
                unloadCollections(project.getORI());

            }
        });

    }

    /**
     * Unload recursively all the collections inside the given ORI.
     *
     * @param container The ORI url of the root container
     */
    private void unloadCollections(ORI container) {

        List<Collection> collections = resourceManager.loadChildren(Collection.class, container);
        for (Collection collection : collections) {
            unload(collection.getORI());
        }

        List<Folder> folders = resourceManager.loadChildren(Folder.class, container);
        for(Folder folder : folders) {
            unloadCollections(folder.getORI());
        }

    }

    @Override
    public boolean isLinkable(Query query, ORI collectionUri) {

        Collection joinCollection = resourceManager.load(Collection.class, collectionUri);

        for (Map.Entry<String, ORI> tpDefine : query.getDefine().entrySet()) {

            ORI tpCollectionUri = tpDefine.getValue().toAbsolute(query.getOn());

            Collection tpJoinCollection = resourceManager.load(Collection.class, tpCollectionUri);

            if (joinCollection.equals(tpJoinCollection)) {
                return true;
            }

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

        Set<ORI> notRegisteredCollections = new HashSet<ORI>();
        for (ORI collectionURI : getQueryCollections(query)) {
            if (!collectionStore.isRegistered(collectionURI)) {
                notRegisteredCollections.add(collectionURI);
            }
        }

        String taskId = Integer.toHexString(query.hashCode());
        Progress progress = getTask(taskId);

        if (progress == null && !notRegisteredCollections.isEmpty()) {
            progress = new Progress(taskId, "Loading collections");

            LOGGER.info("Starting task {}", taskId);
            for (ORI collectionURI : notRegisteredCollections) {

                Project project = resourceManager.getProject(collectionURI.getProjectUrl());
                Collection collection = resourceManager.load(Collection.class, collectionURI);

                if (collection == null) {
                    progress.error("Unknown collection '" + collectionURI + "'");
                    progress.fail();
                } else {

                    String subTaskId = Integer.toHexString(collectionURI.hashCode());

                    if (!runningCollections.containsKey(subTaskId)) {

                        Progress subProgress = new Progress(subTaskId, "Load '" + collectionURI.getPath() + "'");
                        progressManager.addProgress(subProgress);
                        runningCollections.put(subTaskId, subProgress);

                        LOGGER.info("Registering {}", collectionURI);
                        collectionStore.register(collectionURI);

                        LOGGER.info("Submiting store collection '{}'", collectionURI);

                        Loader loader = collection.getLoader();
                        Plugin plugin = project.getPlugin(loader.getPlugin());
                        ICollectionLoader collectionLoader = resourceManager.getLoader(ICollectionLoader.class, plugin, loader);

                        Runnable command = new InsertCollectionRunnable(LoginContext.get(), runningCollections, plugin, collection, collectionLoader, collectionStore);
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

    private Set<ORI> getQueryCollections(Query query) {

        Set<ORI> queryCollections = new HashSet<ORI>();
        ORI onUri = query.getOn();

        for (ORI collectionUri : query.getDefine().values()) {
            queryCollections.add(collectionUri.toAbsolute(onUri));
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
    public void unload(ORI collectionURI) {

        if (collectionStore.isRegistered(collectionURI)) {
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

    @Override
    public String getMount() {
        return "oql";
    }

    public IProgressManager getProgressManager() {
        return progressManager;
    }

    public void setProgressManager(IProgressManager progressManager) {
        this.progressManager = progressManager;
    }
}
