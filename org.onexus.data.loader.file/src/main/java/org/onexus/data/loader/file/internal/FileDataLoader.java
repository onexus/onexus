package org.onexus.data.loader.file.internal;

import org.onexus.data.api.Data;
import org.onexus.data.api.IDataLoader;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;
import org.onexus.resource.api.Plugin;

import java.util.concurrent.Callable;

public class FileDataLoader implements IDataLoader {

    @Override
    public Callable<IDataStreams> newCallable(Progress progress, Plugin plugin, Data data) {
        return new FileCallable(progress, plugin, data);
    }

}
