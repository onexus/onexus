package org.onexus.collection.api;

import org.onexus.data.api.Task;
import org.onexus.resource.api.Plugin;

import java.util.concurrent.Callable;

public interface ICollectionLoader {

    public Callable<IEntitySet> newCallable(Task task, Plugin plugin, Collection collection);
}
