package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class UrlDataStreams implements IDataStreams {

    private Progress progress;
    private List<URL> urls;

    public UrlDataStreams(Progress progress, List<URL> urls) {
        this.progress = progress;
        this.urls = urls;
    }

    @Override
    public Progress getProgress() {
        return progress;
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
