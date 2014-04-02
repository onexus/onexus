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
package org.onexus.resource.manager.internal.providers;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.IResourceValidator;
import org.onexus.resource.manager.internal.PluginLoader;

import java.io.File;
import java.util.List;

public class ProjectProviderFactory {

    private IResourceSerializer serializer;
    private IResourceValidator validator;
    private PluginLoader pluginLoader;
    private FileAlterationMonitor monitor;
    private List<IResourceListener> listeners;

    public ProjectProviderFactory(IResourceSerializer serializer, IResourceValidator validator, PluginLoader pluginLoader, FileAlterationMonitor monitor, List<IResourceListener> listeners) {
        super();

        this.pluginLoader = pluginLoader;
        this.serializer = serializer;
        this.validator = validator;
        this.monitor = monitor;
        this.listeners = listeners;
    }

    public AbstractProjectProvider newProjectProvider(String projectName, String projectUri, File projectFolder) {

        AbstractProjectProvider provider;

        if (projectUri.endsWith(".git")) {
            provider = new GitProjectProvider(projectName, projectUri, projectFolder, monitor, listeners);
        } else if (projectUri.endsWith(".zip")) {
            provider = new ZipProjectProvider(projectName, projectUri, projectFolder, monitor, listeners);
        } else {
            provider = new FolderProjectProvider(projectName, projectUri, projectFolder, monitor, listeners);
        }

        provider.setSerializer(serializer);
        provider.setValidator(validator);
        provider.setPluginLoader(pluginLoader);

        return provider;
    }
}
