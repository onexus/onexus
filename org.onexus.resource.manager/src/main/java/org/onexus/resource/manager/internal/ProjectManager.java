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

import org.apache.commons.io.FilenameUtils;
import org.onexus.data.api.Data;
import org.onexus.resource.api.*;
import org.onexus.resource.api.exceptions.UnserializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

public class ProjectManager {

    private static final Logger log = LoggerFactory.getLogger(ProjectManager.class);
    public final static String ONEXUS_EXTENSION = "onx";
    public final static String ONEXUS_PROJECT_FILE = "onexus-project." + ONEXUS_EXTENSION;

    private IResourceSerializer serializer;
    private String projectUrl;
    private File projectFolder;

    private Project project;
    private Map<ORI, Resource> resources;

    public ProjectManager(IResourceSerializer serializer, String projectUrl, File projectFolder) throws InvalidParameterException {
        super();

        this.serializer = serializer;
        this.projectUrl = projectUrl;
        this.projectFolder = projectFolder;

    }

    public Project getProject() {
        if (project == null) {
            loadProject();
        }

        return project;
    }

    public void loadResources() {

        this.resources = new HashMap<ORI, Resource>();

        Collection<File> files = addFilesRecursive(new ArrayList<File>(), projectFolder);

        for (File file : files) {

            // Skip project file
            if (ONEXUS_PROJECT_FILE.equals(file.getName())) {
                continue;
            }

            Resource resource = loadResource(file);

            if (resource != null) {
                resources.put(resource.getURI(), resource);
            }
        }

    }

    public Resource getResource(ORI resourceUri) {

        if (resourceUri.getPath() == null && projectUrl.equals(resourceUri.getProjectUrl())) {
            return project;
        }

        if (resources == null) {
            loadResources();
        }

        if (!resources.containsKey(resourceUri)) {
            throw new UnsupportedOperationException("Resource '" + resourceUri + "' is not defined in any project.");
        }

        return resources.get(resourceUri);

    }

    public <T extends Resource> List<T> getResourceChildren(IAuthorizationManager authorizationManager, Class<T> resourceType, ORI parentURI) {

        if (resources == null) {
            loadResources();
        }

        List<T> children = new ArrayList<T>();
        for (Resource resource : resources.values()) {
            if (parentURI.isChild(resource.getURI()) && resourceType.isAssignableFrom(resource.getClass())) {
                if (authorizationManager.check(IAuthorizationManager.READ, resource.getURI())) {
                    children.add((T) resource);
                }
            }
        }

        return children;
    }

    public void loadProject() {
        File projectOnx = new File(projectFolder, ONEXUS_PROJECT_FILE);

        if (!projectOnx.exists()) {
            throw new InvalidParameterException("No Onexus project in " + projectFolder.getAbsolutePath());
        }

        this.project = (Project) loadResource(projectOnx);
    }

    private Resource loadResource(File resourceFile) {

        String resourceName;
        Resource resource;

        if (ONEXUS_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(resourceFile.getName()))) {

            resourceName = FilenameUtils.getBaseName(resourceFile.getName());

            try {

                resource = serializer.unserialize(Resource.class, new FileInputStream(resourceFile));

            } catch (FileNotFoundException e) {
                resource = createErrorResource(resourceFile, "File '" + resourceFile.getPath() + "' not found.");
            } catch (UnserializeException e) {
                resource = createErrorResource(resourceFile, "Parsing file " + resourceFile.getPath() + " at line " + e.getLine() + " on " + e.getPath());
            } catch (Exception e) {
                resource = createErrorResource(resourceFile, e.getMessage());
            }

        } else {

            resourceName = resourceFile.getName();

            if (resourceFile.isDirectory()) {
                resource = new Folder();
            } else {
                resource = createDataResource(resourceFile);
            }

        }

        if (resource == null) {
            return null;
        }

        String projectPath = projectFolder.getAbsolutePath() + File.separator;
        String filePath = resourceFile.getAbsolutePath();
        String relativePath = filePath.replace(projectPath, "");

        if (relativePath.equals(ONEXUS_PROJECT_FILE)) {
            relativePath = null;
        } else {
            relativePath = relativePath.replace("." + ONEXUS_EXTENSION, "");
        }

        resource.setURI(new ORI(projectUrl, relativePath));
        return resource;

    }

    private Resource createErrorResource(File resourceFile, String msg) {
        log.error(msg);
        Resource errorResource = createDataResource(resourceFile);
        errorResource.setDescription("ERROR: " + msg);
        return errorResource;
    }

    private Resource createDataResource(File resourceFile) {
        Data data = new Data();
        Loader loader = new Loader();
        loader.setParameters(new ArrayList<Parameter>());
        loader.getParameters().add(new Parameter("data-url", resourceFile.toURI().toString()));
        data.setLoader(loader);
        return data;
    }

    private static Collection<File> addFilesRecursive(Collection<File> files, File parentFolder) {

        if (parentFolder.isDirectory()) {
            File[] inFiles = parentFolder.listFiles();
            if (inFiles != null) {
                for (File file : inFiles) {
                    if (!file.isHidden()) {
                        files.add(file);
                        if (file.isDirectory()) {
                            addFilesRecursive(files, file);
                        }
                    }
                }
            }
        }

        return files;
    }

    public void save(Resource resource) {

        if (resource == null) {
            return;
        }

        if (resource instanceof Project) {
            throw new IllegalArgumentException("Cannot create a project '" + resource.getURI() + "' inside project '" + projectUrl + "'");
        }

        String resourcePath = resource.getURI().getPath();

        File file;
        if (resource instanceof Folder) {
            file = new File(projectFolder, resourcePath);
            file.mkdirs();

            return;
        }


        file = new File(projectFolder, resourcePath + "." + ONEXUS_EXTENSION);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream os = new FileOutputStream(file);
            serializer.serialize(resource, os);
            os.close();

        } catch (IOException e) {
            log.error("Saving resource '" + resource.getURI() + "' in file '" + file.getAbsolutePath() + "'", e);
        }

        if (resources == null) {
            loadResources();
        }

        this.resources.put(resource.getURI(), resource);

    }

}
