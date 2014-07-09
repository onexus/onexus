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

import org.onexus.resource.api.IAuthorizationManager;
import static org.onexus.resource.api.IAuthorizationManager.READ;
import static org.onexus.resource.api.IAuthorizationManager.WRITE;
import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.resource.manager.internal.providers.AbstractProjectProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.BlueprintListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager implements IResourceManager, BlueprintListener, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    // Injected OSGi services
    private IResourceSerializer serializer;

    private IAuthorizationManager authorizationManager;

    private BundleContext context;

    // Loaders and managers
    private PluginLoader pluginLoader;
    private ProjectsContainer projectsContainer;

    public ResourceManager() {
        super();
    }

    @Override
    public Project getProject(String projectUrl) {

        if (!authorizationManager.check(READ, new ORI(projectUrl))) {
            throw new SecurityException("Unauthorized READ access to project '" + projectUrl + "'");
        }

        AbstractProjectProvider projectManager = getProjectProvider(projectUrl);

        if (projectManager == null) {
            return null;
        }

        return projectManager.getProject();
    }

    @Override
    public List<Project> getProjects() {

        List<Project> projects = new ArrayList<Project>();

        for (String projectUrl : projectsContainer.getProjectUrls()) {
            try {

                AbstractProjectProvider provider = projectsContainer.getProjectProvider(projectUrl);
                Project project = provider.getProject();
                if (authorizationManager.check(READ, project.getORI())) {
                    projects.add(project);
                }

            } catch (Exception e) {
                LOGGER.error("Loading project '" + projectUrl + "'", e);
            }
        }

        return projects;
    }

    @Override
    public <T extends Resource> T load(Class<T> resourceType, ORI resourceURI) {
        assert resourceType != null;

        if (resourceURI == null) {
            return null;
        }

        if (!authorizationManager.check(READ, resourceURI)) {
            throw new SecurityException("Unauthorized READ access to '" + resourceURI.toString() + "'");
        }

        AbstractProjectProvider provider = getProjectProvider(resourceURI.getProjectUrl());

        T output = (T) provider.getResource(resourceURI);

        if (output == null) {
            throw new RuntimeException("Resource '" + resourceURI + "' not found.");
        }

        if (!resourceType.isAssignableFrom(output.getClass())) {
            throw new RuntimeException("The resource '" + resourceURI + "' is not a '" + resourceType.getSimpleName() + "' is a '" + output.getClass().getSimpleName() + "'");
        }

        return output;
    }

    @Override
    public <T extends Resource> List<T> loadChildren(Class<T> resourceType, ORI parentURI) {

        if (!authorizationManager.check(READ, parentURI)) {
            throw new SecurityException("Unauthorized READ access to '" + parentURI.toString() + "'");
        }

        AbstractProjectProvider provider = getProjectProvider(parentURI.getProjectUrl());

        return provider.getResourceChildren(authorizationManager, resourceType, parentURI);
    }

    @Override
    public void save(Resource resource) {

        if (resource == null) {
            return;
        }

        if (!authorizationManager.check(WRITE, resource.getORI())) {
            throw new SecurityException("Unauthorized WRITE access to '" + resource.getORI().toString() + "'");
        }

        AbstractProjectProvider provider = getProjectProvider(resource.getORI().getProjectUrl());
        provider.save(resource);
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

                if (pluginLocation.startsWith(bundle.getLocation())) {
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
    public void addResourceListener(IResourceListener resourceListener) {
        projectsContainer.addResourceListener(resourceListener);
    }

    @Override
    public void importProject(String projectName, String projectUri) {
        projectsContainer.importProject(projectName, projectUri);
    }

    @Override
    public void syncProject(String projectURI) {

        if (!authorizationManager.check(READ, new ORI(projectURI, null))) {
            throw new SecurityException("Unauthorized READ access to '" + projectURI + "'");
        }

        AbstractProjectProvider provider = getProjectProvider(projectURI);
        provider.syncProject();
    }

    @Override
    public void updateProject(String projectUrl) {

        if (!authorizationManager.check(WRITE, new ORI(projectUrl, null))) {
            throw new SecurityException("Unauthorized WRITE access to '" + projectUrl + "'");
        }

        AbstractProjectProvider provider = getProjectProvider(projectUrl);
        provider.updateProject();

    }

    public void init() {
        // Projects container
        this.pluginLoader = new PluginLoader(context);
        this.projectsContainer = new ProjectsContainer(serializer, pluginLoader);

    }

    public void destroy() {
        this.projectsContainer.destroy();
    }

    private AbstractProjectProvider getProjectProvider(String projectUrl) {

        AbstractProjectProvider provider = projectsContainer.getProjectProvider(projectUrl);

        if (provider == null) {
            throw new InvalidParameterException("Project '" + projectUrl + "' is not imported");
        }

        return provider;

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

    public IAuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }


    public ProjectsContainer getProjectsContainer() {
        return projectsContainer;
    }

    public void setProjectsContainer(ProjectsContainer projectsContainer) {
        this.projectsContainer = projectsContainer;
    }


    @Override
    public void blueprintEvent(BlueprintEvent blueprintEvent) {

        if (blueprintEvent.getType() == BlueprintEvent.CREATED) {
            this.projectsContainer.bundleCreated(blueprintEvent.getBundle().getBundleId());
        }

    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {

        if (bundleEvent.getType() == BundleEvent.UNINSTALLED) {
            this.projectsContainer.bundleUninstalled(bundleEvent.getBundle().getBundleId());
        }

    }
}
