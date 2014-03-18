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

import org.onexus.collection.api.*;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Progress;
import org.onexus.resource.api.session.LoginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

class InsertCollectionRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertCollectionRunnable.class);

    private Map<String, Progress> tasks;
    private Plugin plugin;
    private Collection collection;
    private ICollectionLoader loader;
    private ICollectionStore store;
    private LoginContext loginContext;

    public InsertCollectionRunnable(LoginContext loginContext, Map<String, Progress> tasks, Plugin plugin, Collection collection, ICollectionLoader loader, ICollectionStore collectionStore) {

        this.loginContext = loginContext;
        this.tasks = tasks;
        this.plugin = plugin;
        this.collection = collection;
        this.loader = loader;
        this.store = collectionStore;
    }

    @Override
    public void run() {

        // Wait all dependencies to finish
        while(isAnyDependencyRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        String taskId = Integer.toHexString(collection.getORI().hashCode());

        LoginContext.set(loginContext, null);

        Progress progress = tasks.get(taskId);

        try {

            Callable<IEntitySet> callable = loader.newCallable(progress, plugin, collection);
            IEntitySet entitySet = callable.call();

            String msg = "Inserting '" + collection.getORI().getPath() + "'";

            progress.run();
            progress.info(msg);
            LOGGER.info(msg);

            store.insert(entitySet);

            msg = "Collection '" + collection.getORI().getPath() + "' inserted.";
            progress.info(msg);
            progress.done();

            LOGGER.info(msg);

        } catch (Exception e) {

            progress.error(e.getMessage());
            LOGGER.error(e.getMessage(), e);

            progress.fail();
            store.deregister(collection.getORI());

        } finally {

            tasks.remove(taskId);
        }
    }

    private boolean isAnyDependencyRunning() {

        if (collection.getLinks() != null) {
            for (Link link : collection.getLinks()) {
                ORI ori = link.getCollection().toAbsolute(collection.getORI());
                String taskId = Integer.toHexString(ori.hashCode());
                if (tasks.containsKey(taskId)) {
                    LOGGER.debug("'" + collection.getORI() + "' stalled waiting for '" + ori + "'");
                    return true;
                }
            }
        }

        return false;
    }

}
