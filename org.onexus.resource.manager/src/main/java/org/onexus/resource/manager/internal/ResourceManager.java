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
package org.onexus.resource.manager.internal;

import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.resources.Project;
import org.onexus.resource.api.resources.Resource;
import org.onexus.resource.api.utils.ResourceUtils;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceManager implements IResourceManager {

    // Injected OSGi services
    private IResourceSerializer serializer;
    private BundleContext context;

    // Loaders and managers
    private PluginLoader pluginLoader;
    private ProjectsContainer projectsContainer;
    private Map<String, ProjectManager> projectManagers;

    public ResourceManager() {
        super();
    }

    public void init() {

        // Projects container
        this.projectsContainer = new ProjectsContainer();

        // Plugin loader
        this.pluginLoader = new PluginLoader(context);

        // Load projects managers
        this.projectManagers = new HashMap<String, ProjectManager>();
        for (String projectUri : this.projectsContainer.getProjectUris()) {
            File projectFolder = projectsContainer.getProjectFolder(projectUri);

            this.projectManagers.put(projectUri, newProjectManager(projectUri, projectFolder));
        }

    }

    private ProjectManager newProjectManager(String projectUri, File projectFolder) {
        ProjectManager projectManager = new ProjectManager(serializer, projectUri, projectFolder);
        projectManager.loadProject();
        this.pluginLoader.load(projectManager.getProject());
        return projectManager;
    }

    @Override
    public Project getProject(String projectUri) {

        ProjectManager projectManager = getProjectManager(projectUri);

        if (projectManager == null) {
            return null;
        }

        return projectManager.getProject();
    }

    @Override
    public List<Project> getProjects() {

        List<Project> projects = new ArrayList<Project>();

        for (ProjectManager projectManager : projectManagers.values()) {
            projects.add(projectManager.getProject());
        }

        return projects;
    }

    @Override
    public <T extends Resource> T load(Class<T> resourceType, String resourceURI) {
        assert resourceType != null;

        if (resourceURI == null) {
            return null;
        }

        resourceURI = ResourceUtils.normalizeUri(resourceURI);

        ProjectManager projectManager = getProjectManager(resourceURI);

        T output = (T) projectManager.getResource(resourceURI);

        return output;
    }

    @Override
    public <T extends Resource> List<T> loadChildren(Class<T> resourceType, String parentURI) {
        assert resourceType != null;
        assert parentURI != null;

        ProjectManager projectManager = getProjectManager(parentURI);

        return projectManager.getResourceChildren(resourceType, parentURI);
    }

    @Override
    public void save(Resource resource) {

        if (resource == null) {
            return;
        }

        ProjectManager projectManager = getProjectManager(resource.getURI());
        projectManager.save(resource);
    }

    @Override
    public void importProject(String projectUri) {
        projectUri = ResourceUtils.normalizeUri(projectUri);
        File projectFolder = projectsContainer.projectImport(projectUri);
        this.projectManagers.put(projectUri, newProjectManager(projectUri, projectFolder));
    }

    @Override
    public void syncProject(String projectURI) {
        ProjectManager projectManager = getProjectManager(projectURI);
        projectManager.loadProject();
        this.pluginLoader.load(projectManager.getProject());
        projectManager.loadResources();
    }

    private ProjectManager getProjectManager(String resourceUri) {

        String projectUri = ResourceUtils.getProjectURI(resourceUri);
        ProjectManager projectManager = projectManagers.get(projectUri);

        if (projectManager == null) {
            throw new InvalidParameterException("Project '" + projectUri + "' is not imported");
        }

        return projectManager;

    }

    public IResourceSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(IResourceSerializer serializer) {
        this.serializer = serializer;
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }


}
