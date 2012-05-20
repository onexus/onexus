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

import org.onexus.core.*;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.onexus.core.utils.FieldLink;
import org.onexus.core.utils.LinkUtils;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.utils.ResourceUtils;
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

    private ITaskManager taskManager;

    private int maxThreads = 1;

    private ExecutorService executorService;
    private Map<String, TaskStatus> runningTasks;

    public CollectionManager() {
        super();
        this.runningTasks = Collections.synchronizedMap(new HashMap<String, TaskStatus>());
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void init() {
        if (executorService instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executorService).setMaximumPoolSize(maxThreads);
        }

        sync();
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

        if (notRegisteredCollections.isEmpty()) {
            return collectionStore.load(query);
        }

        String taskId = Integer.toHexString(query.hashCode());

        TaskStatus taskStatus = getTaskStatus(taskId);

        if (taskStatus == null) {
            taskStatus = new ContainerTaskStatus(taskId, "Storing all query collections (" + query + ")");

            LOGGER.info("Starting task {}", taskId);
            for (String collectionURI : notRegisteredCollections) {

                Collection collection = resourceManager.load(Collection.class, collectionURI);

                if (collection == null) {
                    throw new UnsupportedOperationException("Unknown collection '" + collectionURI +"'");
                }

                taskId = Integer.toHexString(collectionURI.hashCode());
                TaskStatus storeCollection = new TaskStatus(taskId, "Running '" + collection.getName() + "'");

                boolean collectionUpdated = taskManager.preprocessCollection(collection);
                if (collectionUpdated) {
                    resourceManager.save(collection);
                    resourceManager.commit(collection.getURI());
                }

                LOGGER.info("Registering collection {}", collectionURI);
                collectionStore.registerCollection(collectionURI);

                LOGGER.info("Submiting store collection '{}'", collectionURI);
                executorService.submit(new StoreCollection(storeCollection, collectionURI));

                taskStatus.addSubTask(storeCollection);
            }
        }

        IEntityTable partialResults = collectionStore.load(query);
        partialResults.setTaskStatus(taskStatus);

        return partialResults;
    }

    private class StoreCollection implements Runnable {

        private TaskStatus parentTask;
        private String collectionURI;

        public StoreCollection(TaskStatus parentTask, String collectionURI) {
            super();
            this.parentTask = parentTask;
            this.collectionURI = collectionURI;
        }

        @Override
        public void run() {

            try {
                Collection collection = resourceManager.load(Collection.class, collectionURI);

                TaskStatus task = taskManager.submitCollection(collection);

                // Wait until task finish.
                while (!task.isDone()) {
                    parentTask.setLogs(task.getLogs());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.error("While waiting for task.", e);
                        parentTask.addLog("ERROR while waiting for task. " + e.getMessage());
                    }
                    task = taskManager.getTaskStatus(task.getId());
                }

                IEntitySet entitySet = taskManager.getTaskOutput(task.getId());

                LOGGER.info("Task '" + parentTask.getId() + "': Inserting collection {}", collectionURI);
                parentTask.addLog("Inserting collection '" + collectionURI + "'");
                collectionStore.insert(entitySet);
                parentTask.addLog("Collection '" + collectionURI + "' inserted.");
                parentTask.setDone(true);
                LOGGER.info("Task '" + parentTask.getId() + "': Done.");

            } catch (Exception e) {
                LOGGER.error("Task '" + parentTask.getId() + "': Error. " + e.getMessage());
                collectionStore.unregisterCollection(collectionURI);
            }
        }

    }

    private Set<String> getQueryCollections(Query query) {

        Set<String> queryCollections = new HashSet<String>();
        String onUri = query.getOn();

        for (String collectionUri : query.getDefine().values()) {
            queryCollections.add(ResourceUtils.getAbsoluteURI(onUri, collectionUri));
        }

        return queryCollections;
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        TaskStatus task = runningTasks.get(taskId);

        if (task == null) {
            return null;
        }

        if (task.isDone()) {
            runningTasks.remove(taskId);
        }

        return task;
    }

    @Override
    public void sync() {

        // Check that all the registered collections still exists
        for (String collectionURI : collectionStore.getRegisteredCollections()) {
            Collection collection = null;
            try {
                collection = resourceManager.load(Collection.class, collectionURI);
            } catch (RuntimeException e) {
                LOGGER.info("Registered collection '" + collectionURI
                        + "' not found in any ResourceManager. Unregistering it.");
            }
            if (collection == null) {
                collectionStore.unregisterCollection(collectionURI);
            }
        }

    }

    @Override
    public void unload(String collectionURI) {

        if (collectionStore.isRegistered(collectionURI)) {
            // If in future exist multiple collectionStores this action makes
            // not sense!
            collectionStore.unregisterCollection(collectionURI);
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

    public ITaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(ITaskManager taskManager) {
        this.taskManager = taskManager;
    }

}
