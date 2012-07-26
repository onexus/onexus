package org.onexus.collection.api;

import org.onexus.data.api.Progress;
import org.onexus.resource.api.Plugin;

import java.util.concurrent.Callable;

public interface ICollectionLoader {

    public Callable<IEntitySet> newCallable(Progress progress, Plugin plugin, Collection collection);
}
