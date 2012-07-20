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
package org.onexus.resource.api.resources;


import java.util.List;

public class Project extends Resource {

    private List<Repository> repositories;

    private List<Plugin> plugins;

    public Project() {
        super();
    }

    public Repository getRepository(String repositoryId) {

        if (repositories == null || repositoryId == null) {
            return null;
        }

        for (Repository repository : repositories ) {
            if (repositoryId.equals(repository.getId())) {
                return repository;
            }
        }

        return null;

    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public Plugin getPlugin(String pluginId) {

        if (plugins == null || pluginId == null) {
            return null;
        }

        for (Plugin plugin : plugins) {
            if (pluginId.equals(plugin.getId())) {
                return plugin;
            }
        }

        return null;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public String toString() {
        return "Project{" +
                "repositories=" + repositories +
                '}';
    }
}
