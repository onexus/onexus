package org.onexus.data.api.utils;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class UrlInputStreamIterator implements Iterator<InputStream> {

    private Iterator<URL> urls;

    public UrlInputStreamIterator(List<URL> urls) {
        this.urls = urls.iterator();
    }

    @Override
    public boolean hasNext() {
        return urls.hasNext();
    }

    @Override
    public InputStream next() {
        try {
            URL url = urls.next();
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        urls.remove();
    }
}
