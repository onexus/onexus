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
package org.onexus.data.loader.file.internal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.onexus.data.api.Data;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.utils.UrlDataStreams;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Progress;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class FileCallable implements Callable<IDataStreams> {

    private Progress progress;
    private Plugin plugin;
    private Data data;

    private boolean compressed;

    public FileCallable(Progress progress, Plugin plugin, Data data) {
        this.progress = progress;
        this.plugin = plugin;
        this.data = data;

        String format = plugin.getParameter("format");

        if (format != null && format.equals("gz")) {
            compressed = true;
        } else {
            compressed = false;
        }
    }

    @Override
    public IDataStreams call() throws Exception {
        progress.done();
        return new UrlDataStreams(progress, getUrls(plugin, data), compressed);
    }

    private List<URL> getUrls(Plugin plugin, Data data) {

        String location = plugin.getParameter("location");
        String mirror = plugin.getParameter("mirror");

        List<String> paths = data.getLoader().getParameterList("path");

        List<URL> urls = new ArrayList<URL>();

        for (String templatePath : paths) {
            String path = templatePath;
            String fileName = FilenameUtils.getName(path);

            // Check if it is a wildcard filter
            if (fileName.contains("*") || fileName.contains("?")) {

                // Is recursive?
                IOFileFilter dirFilter = null;
                if (path.contains("**/")) {
                    dirFilter = TrueFileFilter.INSTANCE;
                    path = path.replace("**/", "");
                }

                String sourceContainer = location + File.separator + FilenameUtils.getFullPathNoEndSeparator(path);
                File sourceFile = new File(sourceContainer);

                for (File file : (Collection<File>) FileUtils.listFiles(sourceFile, new WildcardFileFilter(fileName), dirFilter)) {
                    try {
                        urls.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                String sourcePath = location + File.separator + path;
                File sourceFile = new File(sourcePath);

                if (sourceFile.exists()) {
                    try {
                        urls.add(sourceFile.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                } else {

                    // Try mirror
                    if (mirror != null) {

                        String remoteFile = mirror + '/' + path;

                        try {
                            URL url = new URL(remoteFile);
                            urls.add(url);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        return urls;
    }
}
