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

import org.onexus.resource.api.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceManager implements IResourceManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

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
    public Project getProject(String projectUrl) {

        ProjectManager projectManager = getProjectManager(projectUrl);

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
    public <T extends Resource> T load(Class<T> resourceType, ORI resourceURI) {
        assert resourceType != null;

        if (resourceURI == null) {
            return null;
        }

        ProjectManager projectManager = getProjectManager(resourceURI.getProjectUrl());

        T output = (T) projectManager.getResource(resourceURI);

        return output;
    }

    @Override
    public <T extends Resource> List<T> loadChildren(Class<T> resourceType, ORI parentURI) {

        ProjectManager projectManager = getProjectManager(parentURI.getProjectUrl());

        return projectManager.getResourceChildren(resourceType, parentURI);
    }

    @Override
    public void save(Resource resource) {

        if (resource == null) {
            return;
        }

        ProjectManager projectManager = getProjectManager(resource.getURI().getProjectUrl());
        projectManager.save(resource);
    }

    @Override
    public <T> T getLoader(Class<T> serviceClass, Plugin plugin, Loader loader) {

        if (plugin == null || plugin.getLocation() == null) {
            String msg = "Plugin '" + loader.getPlugin() + "' not defined in project. ";
            throw new RuntimeException(msg);
        }

        String pluginLocation = plugin.getLocation();

        try {
            for (ServiceReference service : context.getServiceReferences(serviceClass.getName(), null)) {

                Bundle bundle = service.getBundle();

                if (bundle == null) {
                    continue;
                }

                if (pluginLocation.equals(bundle.getLocation())) {
                    return (T) context.getService(service);
                }

            }
        } catch (InvalidSyntaxException e) {
            LOGGER.error("On context.getServiceReferences()", e);
        }

        // TODO Auto-install tool
        String msg = "Plugin for '" + loader + "' not found.";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
    }

    @Override
    public void importProject(String projectUri) {
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

    private ProjectManager getProjectManager(String projectUrl) {

        ProjectManager projectManager = projectManagers.get(projectUrl);

        if (projectManager == null) {
            throw new InvalidParameterException("Project '" + projectUrl + "' is not imported");
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
