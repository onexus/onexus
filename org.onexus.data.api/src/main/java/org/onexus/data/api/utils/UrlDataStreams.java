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

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class UrlDataStreams implements IDataStreams {

    private Progress progress;
    private List<URL> urls;
    private boolean uncompress;

    public UrlDataStreams(Progress progress, List<URL> urls) {
        this(progress, urls, false);
    }

    public UrlDataStreams(Progress progress, List<URL> urls, boolean uncompress) {
        this.progress = progress;
        this.urls = urls;
        this.uncompress = uncompress;
    }

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    public void close() {
        this.urls = null;
    }

    @Override
    public Iterator<InputStream> iterator() {

        if (urls == null) {
            return Collections.EMPTY_LIST.iterator();
        }

        return new UrlInputStreamIterator(urls, uncompress);
    }


}
