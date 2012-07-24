package org.onexus.data.api.utils;

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;

public class EmptyDataStreams implements IDataStreams {

    private Task task;

    public EmptyDataStreams(Task task) {
        this.task = task;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Iterator<InputStream> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
}
