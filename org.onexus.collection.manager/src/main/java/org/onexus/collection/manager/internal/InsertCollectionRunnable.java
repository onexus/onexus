package org.onexus.collection.manager.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionLoader;
import org.onexus.collection.api.ICollectionStore;
import org.onexus.collection.api.IEntitySet;
import org.onexus.data.api.Task;
import org.onexus.resource.api.Plugin;

import java.util.Map;
import java.util.concurrent.Callable;

class InsertCollectionRunnable implements Runnable {

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

            task.getLogger().info("Inserting collection '" + collection.getURI() + "'");

            store.insert(entitySet);

            task.getLogger().info("Collection '" + collection.getURI() + "' inserted.");


        } catch (Exception e) {

            task.getLogger().error(e.getMessage());
            task.setCancelled(true);
            store.deregister(collection.getURI());

        } finally {

            task.setDone(true);
            tasks.remove(collection.getURI());
        }
    }

}
