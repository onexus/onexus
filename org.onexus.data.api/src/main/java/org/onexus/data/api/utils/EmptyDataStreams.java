package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;

public class EmptyDataStreams implements IDataStreams {

    private Progress progress;

    public EmptyDataStreams(Progress progress) {
        this.progress = progress;
    }

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Iterator<InputStream> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
}
