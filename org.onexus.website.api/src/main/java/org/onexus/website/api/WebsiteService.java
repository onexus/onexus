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
import org.onexus.resource.api.IResourceService;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WebsiteService implements IWebsiteService {

    public final static String MOUNT = "web";

    private static final Logger log = LoggerFactory.getLogger(WebsiteService.class);
    private IResourceManager resourceManager;
    private BundleContext context;

    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();


    @Override
    public String getMount() {
        return MOUNT;
    }

    public void init() {

        for (Project project : resourceManager.getProjects()) {

            List<WebsiteConfig> websites = resourceManager.loadChildren(WebsiteConfig.class, new ORI(project.getURL(), null));

            for (WebsiteConfig website : websites) {
                registerWebsite(project.getName(), website);

                //TODO Allow multiple websites per project
                break;
            }

        }

    }

    private void registerWebsite(String name, WebsiteConfig website) {

        Properties props = new Properties();
        props.put(Constants.APPLICATION_NAME, "web_" + name.replace('/', '_'));
        props.put(Constants.MOUNTPOINT, "web/" + name);

        registrations.add(context.registerService(
                WebApplicationFactory.class.getName(),
                new WebsiteApplicationFactory(website.getName(), website.getURI().toString()),
                props
        ));

        log.info("Registering website /web/" + name);

    }

    public void destroy() {

        for (ServiceRegistration registration : registrations) {
            registration.unregister();
        }

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
