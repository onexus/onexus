package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class SingleDataStreams implements IDataStreams {

    private Task task;
    private InputStream inputStream;

    public SingleDataStreams(Task task, InputStream inputStream) {
        this.task = task;
        this.inputStream = inputStream;
    }

    @Override
    public Task getTask() {
        return task;
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
