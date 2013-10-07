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
package org.onexus.data.manager.internal;

import org.onexus.data.api.Data;
import org.onexus.data.api.IDataLoader;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.utils.EmptyDataStreams;
import org.onexus.data.api.utils.UrlDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Progress;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DataManager implements IDataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);

    private IResourceManager resourceManager;


    public DataManager() {
        super();
    }

    @Override
    public IDataStreams load(ORI dataURI) {

        Project project = resourceManager.getProject(dataURI.getProjectUrl());
        Data data = null;
        boolean uncompress = false;

        try {
            data = resourceManager.load(Data.class, dataURI);
        } catch (ResourceNotFoundException e) {
        }

        // Look for the gzip version
        if (data == null) {

            try {
                data = resourceManager.load(Data.class, new ORI(dataURI.getProjectUrl(), dataURI.getPath() + ".gz"));
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException(dataURI);
            }

            uncompress = true;
        }

        Loader loader = data.getLoader();
        Progress progress = new Progress(Integer.toHexString(dataURI.hashCode()), "Retrive '" + dataURI.getPath() + "'");
        progress.run();

        Plugin plugin = project.getPlugin(loader.getPlugin());

        // If there is no plugin to load the data
        // check for the 'data-url' parameters directly
        if (plugin == null) {
            List<String> dataUrls = loader.getParameterList("data-url");
            List<URL> urls = new ArrayList<URL>(dataUrls.size());
            for (String dataUrl : dataUrls) {
                try {
                    URL url = new URL(dataUrl);
                    urls.add(url);
                } catch (MalformedURLException e) {
                    progress.error("Malformed URL " + dataUrl);
                    progress.fail();
                    return new EmptyDataStreams(progress);
                }
            }

            progress.done();
            return new UrlDataStreams(progress, urls, uncompress);
        }


        IDataLoader dataLoader = resourceManager.getLoader(IDataLoader.class, plugin, data.getLoader());

        //TODO use a IDataStore as a cache
        //TODO run loaders asynchronously

        Callable<IDataStreams> callable = dataLoader.newCallable(progress, plugin, data);

        IDataStreams dataStreams = null;
        try {
            dataStreams = callable.call();
        } catch (Exception e) {
            LOGGER.error("Error loading '" + dataURI.toString() + "'", e);
            progress.error(e.getMessage());
            progress.fail();
            return new EmptyDataStreams(progress);
        }

        return dataStreams;
    }


    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public long size(ORI dataURI) {

        Project project = resourceManager.getProject(dataURI.getProjectUrl());
        Data data = resourceManager.load(Data.class, dataURI);

        Loader loader = data.getLoader();

        Plugin plugin = project.getPlugin(loader.getPlugin());

        // If there is no plugin to load the data
        // check for the 'data-url' parameters directly
        if (plugin == null) {
            List<String> dataUrls = loader.getParameterList("data-url");
            List<URL> urls = new ArrayList<URL>(dataUrls.size());
            for (String dataUrl : dataUrls) {
                try {
                    URL url = new URL(dataUrl);
                    urls.add(url);
                } catch (MalformedURLException e) {
                    LOGGER.error("Malformed URL '" + dataUrl + "'", e);
                }
            }

            long size = 0;

            for (URL url : urls) {

                long oneSize = getContentLength(url);
                if (oneSize == -1) {
                    return -1;
                }

                size += oneSize;
            }

            return size;
        }

        IDataLoader dataLoader = resourceManager.getLoader(IDataLoader.class, plugin, data.getLoader());

        return dataLoader.size();
    }

    private long getContentLength(URL url) {
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            return -1;
        }
        return file.length();
    }

    @Override
    public String getMount() {
        return "ds";
    }
}
