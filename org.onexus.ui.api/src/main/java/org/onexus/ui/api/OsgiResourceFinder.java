/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
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
