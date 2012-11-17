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

import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.manager.internal.PluginLoader;

import java.io.File;

public class ProjectProviderFactory {

    private IResourceSerializer serializer;
    private PluginLoader pluginLoader;

    public ProjectProviderFactory(IResourceSerializer serializer, PluginLoader pluginLoader) {
        super();

        this.pluginLoader = pluginLoader;
        this.serializer = serializer;
    }

    public ProjectProvider newProjectProvider(String projectName, String projectUri, File projectFolder) {

        ProjectProvider provider;

        if (projectUri.endsWith(".git")) {
            provider = new GitProjectProvider(projectName, projectUri, projectFolder);
        } else if (projectUri.endsWith(".zip")) {
            provider = new ZipProjectProvider(projectName, projectUri, projectFolder);
        } else {
            provider = new FolderProjectProvider(projectName, projectUri, projectFolder);
        }

        provider.setSerializer(serializer);
        provider.setPluginLoader(pluginLoader);

        return provider;
    }
}
