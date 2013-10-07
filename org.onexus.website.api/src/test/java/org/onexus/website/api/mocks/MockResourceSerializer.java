package org.onexus.website.api.mocks;

import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.exceptions.UnserializeException;

import java.io.InputStream;
import java.io.OutputStream;

public class MockResourceSerializer implements IResourceSerializer {

    @Override
    public String getMediaType() {
        return "text/xml";
    }

    @Override
    public void serialize(Object resource, OutputStream output) {

    }

    @Override
    public <T> T unserialize(Class<T> resourceType, InputStream input) throws UnserializeException {
        return null;
    }

    @Override
    public void register(Class<?> type) {

    }
}
