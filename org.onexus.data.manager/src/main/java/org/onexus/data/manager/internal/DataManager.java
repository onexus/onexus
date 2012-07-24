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

import org.onexus.data.api.*;
import org.onexus.data.api.utils.EmptyDataStreams;
import org.onexus.data.api.utils.UrlDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DataManager implements IDataManager {

    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private IResourceManager resourceManager;


    public DataManager() {
        super();
    }

    @Override
    public IDataStreams load(String dataURI) {

        Project project = resourceManager.getProject(ResourceUtils.getProjectURI(dataURI));
        Data data = resourceManager.load(Data.class, dataURI);

        Task task = new Task(dataURI);
        Loader loader = data.getLoader();
        Plugin plugin = project.getPlugin(loader.getPlugin());

        // If there is no plugin to load the data
        // check for the 'data-url' parameters directly
        if (plugin == null) {
            List<String> dataUrls = loader.getParameterList("data-url");
            List<URL> urls = new ArrayList<URL>(dataUrls.size());
            for (String  dataUrl : dataUrls) {
                try {
                    URL url = new URL(dataUrl);
                    urls.add(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            return new UrlDataStreams(task, urls);
        }



        IDataLoader dataLoader = resourceManager.getLoader(IDataLoader.class, plugin, data.getLoader());

        //TODO use a IDataStore as a cache
        //TODO run loaders asynchronously

        Callable<IDataStreams> callable = dataLoader.newCallable(task, plugin, data);

        IDataStreams dataStreams = null;
        try {
            dataStreams = callable.call();
        } catch (Exception e) {

            task.getLogger().error(e.getMessage());
            task.setCancelled(true);
            dataStreams = new EmptyDataStreams(task);

        } finally {
            dataStreams.getTask().setDone(true);
        }

        return dataStreams;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


}
