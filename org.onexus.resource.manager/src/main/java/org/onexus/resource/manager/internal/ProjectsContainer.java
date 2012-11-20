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

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.Project;
import org.onexus.resource.manager.internal.providers.ProjectProvider;
import org.onexus.resource.manager.internal.providers.ProjectProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ProjectsContainer {

    private static final Logger log = LoggerFactory.getLogger(ProjectsContainer.class);
    private final static String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    private final static String ONEXUS_PROJECTS_SETTINGS = "projects.ini";
    private final static String ONEXUS_PROJECTS_FOLDER = ONEXUS_FOLDER + File.separator + "projects";

    private IResourceSerializer serializer;
    private PluginLoader pluginLoader;
    private File propertiesFile;
    private FileAlterationMonitor monitor;

    private Properties properties;
    private ProjectProviderFactory providerFactory;
    private Map<String, ProjectProvider> providers;

    // Listeners
    private List<IResourceListener> listeners = new ArrayList<IResourceListener>();

    public ProjectsContainer(IResourceSerializer serializer, PluginLoader pluginLoader) {
        super();

        this.serializer = serializer;
        this.pluginLoader = pluginLoader;

        File onexusFolder = new File(ONEXUS_FOLDER);
        File projectsFolder = new File(ONEXUS_PROJECTS_FOLDER);
        propertiesFile = new File(onexusFolder, ONEXUS_PROJECTS_SETTINGS);

        if (!onexusFolder.exists()) {
            onexusFolder.mkdir();
        }

        if (!projectsFolder.exists()) {
            projectsFolder.mkdir();
        }

        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }

            this.providerFactory = new ProjectProviderFactory(serializer, pluginLoader);
            this.providers = new HashMap<String, ProjectProvider>();

            init();
        } catch (IOException e) {
            throw new RuntimeException("Loading projects file '" + ONEXUS_PROJECTS_SETTINGS + "'", e);
        }


        FileAlterationObserver observer = new FileAlterationObserver(onexusFolder, FileFilterUtils.nameFileFilter(ONEXUS_PROJECTS_SETTINGS));
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                try {
                    ProjectsContainer.this.init();
                } catch (IOException e) {
                    log.error("Loading projects file '" + ONEXUS_PROJECTS_SETTINGS + "'", e);
                }
            }
        });

        monitor = new FileAlterationMonitor(2000);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            log.error("On start projects file monitor", e);
        }

    }

    public void destroy() {
        try {
            monitor.stop();


        } catch (Exception e) {
            log.error("On stop projects file monitor", e);
        }
    }

    private void init() throws IOException {
        this.properties = new Properties();

        properties.load(new FileInputStream(propertiesFile));

        for (String projectUrl : getProjectUrls()) {

            String projectPath = null;
            String projectName = null;
            try {

                String projectProperty[] = properties.getProperty(projectUrl).split(",");
                projectPath = projectProperty[0];
                projectName = (projectProperty.length == 2 ? projectProperty[1] : Integer.toHexString(projectUrl.hashCode()));

                File projectFolder = new File(projectPath);

                ProjectProvider previousProvider = providers.get(projectUrl);

                if (previousProvider != null) {
                    if (previousProvider.getProjectFolder().equals(projectFolder) && previousProvider.getProject().getName().equals(projectName)) {

                        // This project is already registered, skip it
                        continue;
                    }
                }

                ProjectProvider provider = providerFactory.newProjectProvider(projectName, projectUrl, projectFolder);
                providers.put(projectUrl, provider);

                if (previousProvider == null) {
                    onProjectCreate(provider.getProject());
                } else {
                    onProjectChange(provider.getProject());
                }
            } catch (Exception e) {
                log.error("Loading project '" + projectUrl + "' named '" + projectName + "' at " + projectPath, e);
            }
        }

        // Remove deleted projects
        List<String> deletedProjects = new ArrayList<String>(providers.keySet());
        deletedProjects.removeAll(getProjectUrls());
        for (String deletedProject : deletedProjects) {
            onProjectDelete(providers.get(deletedProject).getProject());
            providers.remove(deletedProject);
        }
    }

    public Collection<String> getProjectUrls() {
        return properties.stringPropertyNames();
    }

    public ProjectProvider getProjectProvider(String projectUri) {
        return providers.get(projectUri);
    }

    public ProjectProvider importProject(String projectName, String projectUri) {

        File defaultProjectFolder = newProjectFolder(projectUri);

        ProjectProvider provider = providerFactory.newProjectProvider(projectName, projectUri, defaultProjectFolder);

        if (provider != null) {
            providers.put(projectUri, provider);
            properties.setProperty(projectUri, provider.getProjectFolder().getAbsolutePath() + "," + projectName);

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

    void addResourceListener(IResourceListener resourceListener) {
        listeners.add(resourceListener);
    }

    private void onProjectCreate(Project project) {
        log.info("Project '" + project.getName() + "' created.");

        for (IResourceListener listener : listeners) {
            listener.onProjectCreate(project);
        }
    }

    private void onProjectChange(Project project) {
        log.info("Project '" + project.getName() + "' changed.");

        for (IResourceListener listener : listeners) {
            listener.onProjectChange(project);
        }
    }

    private void onProjectDelete(Project project) {
        log.info("Project '" + project.getName() + "' deleted.");

        for (IResourceListener listener : listeners) {
            listener.onProjectDelete(project);
        }
    }

}
