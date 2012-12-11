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
package org.onexus.website.api;

import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.utils.ResourceListener;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WebsiteService implements IWebsiteService {

    public final static String MOUNT = "web";

    private static final Logger log = LoggerFactory.getLogger(WebsiteService.class);
    private IResourceManager resourceManager;
    private BundleContext context;

    private Map<String, ServiceRegistration> registrations = new HashMap<String, ServiceRegistration>();


    @Override
    public String getMount() {
        return MOUNT;
    }

    public void bind(IResourceManager resourceManager) {

        if (!registrations.isEmpty()) {
            destroy();
        }

        for (Project project : resourceManager.getProjects()) {
            registerProject(project);
        }

        resourceManager.addResourceListener(new ResourceListener() {

            @Override
            public void onProjectCreate(Project project) {
                registerProject(project);
            }

            @Override
            public void onProjectChange(Project project) {

                String projectUrl = project.getURL();

                if (registrations.containsKey(projectUrl)) {

                    ServiceRegistration registration = registrations.get(projectUrl);
                    registration.unregister();

                }

                registerProject(project);
            }

            @Override
            public void onProjectDelete(Project project) {

                String projectUrl = project.getURL();

                if (registrations.containsKey(projectUrl)) {

                    ServiceRegistration registration = registrations.get(projectUrl);

                    log.info("Unregistering website /web/" + project.getName());
                    registration.unregister();
                }

            }
        });
    }

    public void unbind(IResourceManager resourceManager) {
        destroy();
    }

    public void destroy() {
        log.info("Unregistering all websites.");
        for (ServiceRegistration registration : registrations.values()) {
            registration.unregister();
        }
        registrations.clear();
    }

    private void registerProject(Project project) {

        List<WebsiteConfig> websites = resourceManager.loadChildren(WebsiteConfig.class, new ORI(project.getURL(), null));

        for (WebsiteConfig website : websites) {
            registerWebsite(project.getName(), website);

            //TODO Allow multiple websites per project
            break;
        }
    }

    private void registerWebsite(String name, WebsiteConfig website) {

        String projectUrl = website.getURI().getProjectUrl();

        Properties props = new Properties();
        props.put(Constants.APPLICATION_NAME, "web_" + name.replace('/', '_'));
        props.put(Constants.MOUNTPOINT, "web/" + name);

        registrations.put(projectUrl, context.registerService(
                WebApplicationFactory.class.getName(),
                new WebsiteApplicationFactory(website.getName(), website.getURI().toString()),
                props
        ));

        log.info("Registering website /web/" + name);

    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
