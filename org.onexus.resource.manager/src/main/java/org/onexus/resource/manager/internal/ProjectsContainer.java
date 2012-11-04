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

import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.manager.internal.providers.ProjectProvider;
import org.onexus.resource.manager.internal.providers.ProjectProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProjectsContainer {

    private static final Logger log = LoggerFactory.getLogger(ProjectsContainer.class);
    private final static String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    private final static String ONEXUS_PROJECTS_SETTINGS = "projects.ini";
    private final static String ONEXUS_PROJECTS_FOLDER = ONEXUS_FOLDER + File.separator + "projects";

    private Properties properties;
    private ProjectProviderFactory providerFactory;
    private Map<String, ProjectProvider> providers = new HashMap<String, ProjectProvider>();

    public ProjectsContainer(IResourceSerializer serializer, PluginLoader pluginLoader) {
        super();

        this.providerFactory = new ProjectProviderFactory(serializer, pluginLoader);

        this.properties = new Properties();

        File onexusFolder = new File(ONEXUS_FOLDER);
        File projectsFolder = new File(ONEXUS_PROJECTS_FOLDER);
        File propertiesFile = new File(onexusFolder, ONEXUS_PROJECTS_SETTINGS);

        try {

            if (!onexusFolder.exists()) {
                onexusFolder.mkdir();
            }

            if (!projectsFolder.exists()) {
                projectsFolder.mkdir();
            }

            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }

            properties.load(new FileInputStream(propertiesFile));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (String projectUrl : getProjectUrls()) {
            File projectFolder = new File(properties.getProperty(projectUrl));
            ProjectProvider provider = providerFactory.newProjectProvider(projectUrl, projectFolder);
            providers.put(projectUrl, provider);
        }
    }

    public Collection<String> getProjectUrls() {
        return properties.stringPropertyNames();
    }

    public ProjectProvider getProjectProvider(String projectUri) {
        return providers.get(projectUri);
    }

    public ProjectProvider importProject(String projectUri) {

        File defaultProjectFolder = newProjectFolder(projectUri);

        ProjectProvider provider = providerFactory.newProjectProvider(projectUri, defaultProjectFolder);

        if (provider != null) {
            providers.put(projectUri, provider);
            properties.setProperty(projectUri, provider.getProjectFolder().getAbsolutePath());

            try {
                properties.store(new FileOutputStream(new File(new File(ONEXUS_FOLDER), ONEXUS_PROJECTS_SETTINGS)), null);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return provider;
    }

    private File newProjectFolder(String projectUri) {
        return new File(new File(ONEXUS_PROJECTS_FOLDER), Integer.toHexString(projectUri.hashCode()));
    }
}
