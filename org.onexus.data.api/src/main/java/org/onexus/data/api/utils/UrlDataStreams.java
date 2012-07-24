package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Task;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class UrlDataStreams implements IDataStreams {

    private Task task;
    private List<URL> urls;

    public UrlDataStreams(Task task, List<URL> urls) {
        this.task = task;
        this.urls = urls;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void close() {
        this.urls = null;
    }

    @Override
    public Iterator<InputStream> iterator() {

        if (urls == null) {
            return Collections.EMPTY_LIST.iterator();
        }

        return new UrlInputStreamIterator(urls);
    }


}
