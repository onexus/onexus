package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class SingleDataStreams implements IDataStreams {

    private Progress progress;
    private InputStream inputStream;

    public SingleDataStreams(Progress progress, InputStream inputStream) {
        this.progress = progress;
        this.inputStream = inputStream;
    }

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    @Override
    public Iterator<InputStream> iterator() {
        return Arrays.asList(inputStream).iterator();
    }
}
