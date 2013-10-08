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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.resource.api.utils.ResourceListener;
import org.onexus.ui.authentication.jaas.JaasSignInPage;
import org.onexus.ui.authentication.persona.PersonaSignInPage;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class WebsiteService implements IWebsiteService {

    public static final String MOUNT = "web";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteService.class);

    private IResourceManager resourceManager;
    private ICollectionManager collectionManager;

    private List<ISignInPage> signInPages;

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
        try {
            LoginContext.set(LoginContext.SERVICE_CONTEXT, null);

            for (Project project : resourceManager.getProjects()) {
                registerProject(project);
            }

        } finally {
            LoginContext.set(LoginContext.ANONYMOUS_CONTEXT, null);
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

                    LOGGER.info("Unregistering website /web/" + project.getName());
                    registration.unregister();
                }

            }
        });
    }

    public void unbind(IResourceManager resourceManager) {
        destroy();
    }

    public void destroy() {
        LOGGER.info("Unregistering all websites.");
        for (ServiceRegistration registration : registrations.values()) {
            registration.unregister();
        }
        registrations.clear();
    }

    private void registerProject(Project project) {

        try {
            LoginContext.set(LoginContext.SERVICE_CONTEXT, null);

            List<WebsiteConfig> websites = resourceManager.loadChildren(WebsiteConfig.class, new ORI(project.getURL(), null));

            for (WebsiteConfig website : websites) {
                registerWebsite(project.getName(), website);

                //TODO Allow multiple websites per project
                break;
            }
        } finally {
            LoginContext.set(LoginContext.ANONYMOUS_CONTEXT, null);
        }
    }

    private void registerWebsite(String name, WebsiteConfig website) {

        Class<? extends WebPage> signInPageClass = getSignInPageClass(website);

        String projectUrl = website.getORI().getProjectUrl();

        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.APPLICATION_NAME, "web_" + name.replace('/', '_'));
        props.put(Constants.MOUNTPOINT, "web/" + name);

        registrations.put(projectUrl, context.registerService(
                WebApplicationFactory.class.getName(),
                new WebsiteApplicationFactory(website.getName(), website.getORI().toString(), signInPageClass),
                props)
        );

        LOGGER.info("Registering website /web/" + name);

        // Force to load all the collections inside the website project
        loadCollections(new ORI(website.getORI().getProjectUrl()));

    }

    /**
     * Load recursively all the collections inside the given ORI.
     *
     * @param container The ORI url of the root container
     */
    private void loadCollections(ORI container) {

        List<Collection> collections = resourceManager.loadChildren(Collection.class, container);
        for (Collection collection : collections) {
            Query emptyQuery = new Query();
            emptyQuery.addDefine("c", collection.getORI());
            emptyQuery.setFrom("c");
            emptyQuery.setOffset(0);
            emptyQuery.setCount(0);
            IEntityTable table = collectionManager.load(emptyQuery);
            table.close();
        }

        List<Folder> folders = resourceManager.loadChildren(Folder.class, container);
        for (Folder folder : folders) {
            loadCollections(folder.getORI());
        }

    }

    private Class<? extends WebPage> getSignInPageClass(WebsiteConfig website) {

        String signInPageId = website.getSignInPage();

        if (!Strings.isEmpty(signInPageId) && signInPages != null) {
            for (ISignInPage signInPage : signInPages) {
                if (signInPageId.equals(signInPage.getId())) {
                    return signInPage.getPageClass();
                }
            }
        }

        boolean usePersona = Boolean.parseBoolean(System.getProperty("org.onexus.ui.authentication.persona", "false"));
        if (usePersona) {
            return PersonaSignInPage.class;
        }

        return JaasSignInPage.class;

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

    public ICollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void setCollectionManager(ICollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public List<ISignInPage> getSignInPages() {
        return signInPages;
    }

    public void setSignInPages(List<ISignInPage> signInPages) {
        this.signInPages = signInPages;
    }
}
