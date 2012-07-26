package org.onexus.data.api;

import java.io.IOException;
import java.io.InputStream;

public interface IDataStreams extends Iterable<InputStream>, IProgressable {

    void close() throws IOException;

}
