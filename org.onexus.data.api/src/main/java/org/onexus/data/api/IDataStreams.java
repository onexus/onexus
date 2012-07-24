package org.onexus.data.api;

import java.io.IOException;
import java.io.InputStream;

public interface IDataStreams extends Iterable<InputStream> {

    Task getTask();

    void close() throws IOException;

}
