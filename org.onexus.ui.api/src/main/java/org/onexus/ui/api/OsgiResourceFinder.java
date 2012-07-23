package org.onexus.ui.api;

import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;

import java.net.URL;

public class OsgiResourceFinder implements IResourceFinder {
    @Override
    public IResourceStream find(Class<?> aClass, String path) {
        ClassLoader classLoader = aClass.getClassLoader();
        if (classLoader != null)
        {
            URL url = classLoader.getResource(path);
            if (url != null)
            {
                return new UrlResourceStream(url);
            }
        }
        return null;
    }
}
