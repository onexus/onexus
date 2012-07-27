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
