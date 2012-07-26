package org.onexus.collection.manager.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionLoader;
import org.onexus.collection.api.ICollectionStore;
import org.onexus.collection.api.IEntitySet;
import org.onexus.data.api.Task;
import org.onexus.resource.api.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

class InsertCollectionRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(InsertCollectionRunnable.class);
    private Map<String, Task> tasks;
    private Plugin plugin;
    private Collection collection;
    private ICollectionLoader loader;
    private ICollectionStore store;

    public InsertCollectionRunnable(Map<String, Task> tasks, Plugin plugin, Collection collection, ICollectionLoader loader, ICollectionStore collectionStore) {

        this.tasks = tasks;
        this.plugin = plugin;
        this.collection = collection;
        this.loader = loader;
        this.store = collectionStore;
    }

    @Override
    public void run() {

        Task task = tasks.get(collection.getURI());

        try {

            Callable<IEntitySet> callable = loader.newCallable(task, plugin, collection);
            IEntitySet entitySet = callable.call();

            String msg = "Inserting collection '" + collection.getURI() + "'";
            task.getLogger().info(msg);
            log.info(msg);

            store.insert(entitySet);

            msg = "Collection '" + collection.getURI() + "' inserted.";
            task.getLogger().info(msg);
            log.info(msg);

        } catch (Exception e) {

            task.getLogger().error(e.getMessage());
            log.error(e.getMessage(), e);

            task.setCancelled(true);
            store.deregister(collection.getURI());

        } finally {

            task.setDone(true);
            tasks.remove(collection.getURI());
        }
    }

}
