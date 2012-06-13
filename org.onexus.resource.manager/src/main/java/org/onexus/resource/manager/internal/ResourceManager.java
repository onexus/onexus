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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.exceptions.UnserializeException;
import org.onexus.core.resources.*;
import org.onexus.core.utils.ResourceUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.Collection;
import java.util.regex.Pattern;

public class ResourceManager implements IResourceManager {

    @SuppressWarnings("rawtypes")
    public final static Class[] DEFAULT_CONTAINER_RESOURCES = {Project.class, Folder.class};
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);
    private final static String ONEXUS_CONTAINER_PREFIX = "onexus-";
    private final static String ONEXUS_FILE_EXTENSION = "onx";

    // Config properties
    private String baseUrl;
    private String basePath;
    private IResourceSerializer serializer;
    private BundleContext context;

    //TODO Make this Session dependent
    private transient boolean checkedout = false;
    private transient Map<String, String> uriToAbsolutePath = new HashMap<String, String>();
    private transient Map<String, String> projectsPaths = new HashMap<String, String>();
    private transient Map<String, Resource> resources = new HashMap<String, Resource>();
    private transient Set<String> uncommitRemoves = new HashSet<String>();
    private transient Set<String> uncommitSaves = new HashSet<String>();

    public ResourceManager() {
        super();
    }

    public void init() {
        checkout();
    }

    private void loadProjects() {
        this.projectsPaths.clear();

        Properties projects = loadProperties();

        for (String projectName : projects.stringPropertyNames()) {
            String projectPath = projects.getProperty(projectName);
            this.projectsPaths.put(this.baseUrl + "/" + projectName, projectPath);
        }

    }

    private boolean isValidURI(String resourceURI) {
        return getProjectPathEntry(resourceURI) != null;
    }

    private Map.Entry<String, String> getProjectPathEntry(String resourceURI) {

        if (resourceURI == null) {
            return null;
        }

        for (Map.Entry<String, String> ws : this.projectsPaths.entrySet()) {
            if (resourceURI.startsWith(ws.getKey())) {
                return ws;
            }
        }
        return null;
    }

    private void autoCheckout() {
        if (!checkedout) {
            checkout();
            this.checkedout = true;
        }
    }

    @Override
    public void checkout() {

        loadProjects();

        for (Map.Entry<String, String> ws : this.projectsPaths.entrySet()) {
            File folder = new File(ws.getValue());

            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                Resource project = checkoutFile(new File(folder, ONEXUS_CONTAINER_PREFIX + "project." + ONEXUS_FILE_EXTENSION), ws.getKey(), ws.getValue());
                loadPlugins((Project) project);
            } catch (Exception e) {
                LOGGER.error("Loading project plugins", e);
            }


            Collection<File> files = addFilesRecursive(new ArrayList<File>(), folder);

            for (File file : files) {

                Resource resource = null;

                if (ONEXUS_FILE_EXTENSION.equals(FilenameUtils.getExtension(file.getName()))) {

                    try {
                        resource = checkoutFile(file, ws.getKey(), ws.getValue());
                    } catch (FileNotFoundException e) {
                        LOGGER.error("File '" + file.getPath() + "' not found.");
                        continue;
                    } catch (UnserializeException e) {
                        String msg = "Parsing file " + file.getPath() + " at line " + e.getLine() + " on " + e.getPath();
                        LOGGER.error(msg);
                        String relativePath = file.getPath().replaceFirst(Pattern.quote(folder.getPath() + File.separator), "");
                        resource = new Data("resource", relativePath);
                        String resourceURI = buildURIFromFile(file, ws.getKey(), ws.getValue());
                        resource.setURI(resourceURI);
                        resource.setName(ResourceUtils.getResourceName(resourceURI));
                        resource.setDescription("ERROR: " + msg);
                    } catch (Exception e) {
                        String relativePath = file.getPath().replaceFirst(Pattern.quote(folder.getPath() + File.separator), "");
                        resource = new Data("resource", relativePath);
                        String resourceURI = buildURIFromFile(file, ws.getKey(), ws.getValue());
                        resource.setURI(resourceURI);
                        resource.setName(ResourceUtils.getResourceName(resourceURI));
                        resource.setDescription(e.getMessage());
                    }

                } else {
                    if (file.isDirectory()) {
                        resource = new Folder();
                    } else {
                        String relativePath = file.getPath().replaceFirst(Pattern.quote(folder.getPath() + File.separator), "");
                        resource = new Data("resource", relativePath);
                    }

                    String resourceURI = buildURIFromFile(file, ws.getKey(), ws.getValue());
                    resource.setURI(resourceURI);

                    String resourceName = FilenameUtils.getName(file.getAbsolutePath());
                    resource.setName(resourceName);
                }

                if (resource instanceof Project) {
                    Project project = (Project) resource;
                    if (project.getRepositories() == null) {
                        project.setRepositories(new ArrayList<Repository>());
                    }

                    project.getRepositories().add(new Repository("resource", projectsPaths.get(project.getURI())));
                }

                resources.put(resource.getURI(), resource);
                uriToAbsolutePath.put(resource.getURI(), file.getAbsolutePath());
            }
        }
    }

    private Collection<File> addFilesRecursive(Collection<File> files, File parentFolder) {

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

    private Resource checkoutFile(File file, String projectURI, String projectPath) throws FileNotFoundException {

        Resource resource;
        resource = serializer.unserialize(Resource.class, new FileInputStream(file));

        String resourceURI = buildURIFromFile(file, projectURI, projectPath);
        resource.setURI(resourceURI);
        resource.setName(ResourceUtils.getResourceName(resourceURI));

        return resource;
    }

    @Override
    public void commit(String resourceURI) {

        autoCheckout();

        File file = null;
        try {

            if (uncommitSaves.contains(resourceURI)) {
                Resource resource = resources.get(resourceURI);

                // Remove 'resource' repository
                if (resource instanceof Project) {
                    Project project = (Project) resource;

                    if (project.getRepositories() != null) {
                        Repository resourceRepository = null;
                        for (Repository r : project.getRepositories()) {
                            if ("resource".equals(r.getId())) {
                                resourceRepository = r;
                                break;
                            }
                        }

                        if (resourceRepository != null) {
                            project.getRepositories().remove(resourceRepository);
                        }
                    }

                }

                // Don't save 'resource' repository data
                if (resource instanceof Data) {
                    Data data = (Data) resource;
                    if ("resource".equals(data.getRepository())) {
                        return;
                    }
                }

                file = buildFile(resource, getProjectPathEntry(resource.getURI()));

                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                String keepURI = resource.getURI();
                String keepName = resource.getName();
                resource.setURI(null);
                resource.setName(null);
                serializer.serialize(resource, new FileOutputStream(file));
                resource.setURI(keepURI);
                resource.setName(keepName);
                uncommitSaves.remove(resourceURI);
            }

            if (uncommitRemoves.contains(resourceURI)) {
                String filePath = uriToAbsolutePath.get(resourceURI);

                if (filePath != null) {
                    File resourceFile = new File(filePath);
                    File container = resourceFile.getParentFile();
                    FileUtils.deleteQuietly(new File(filePath));

                    if (container.isDirectory()) {
                        if (container.list().length == 0) {
                            container.delete();
                        }
                    }

                    uriToAbsolutePath.remove(resourceURI);
                }
                uncommitRemoves.remove(resourceURI);

            }

        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
            throw new RuntimeException("File '" + String.valueOf(file) + "' not found.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Error commiting file '" + String.valueOf(file) + "'.");
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T load(Class<T> resourceType, String resourceURI) {
        assert resourceType != null;
        assert resourceURI != null;

        autoCheckout();

        // Check that it's a resourceURI of our workspace
        if (resourceURI == null || !isValidURI(resourceURI)) {
            return null;
        }

        // Remove ending separator if exists
        if (resourceURI.charAt(resourceURI.length() - 1) == Resource.SEPARATOR) {
            resourceURI = resourceURI.substring(0, resourceURI.length() - 1);
        }

        // Navigate the workspace and return the resource
        T output = (T) resources.get(resourceURI);

        return output;
    }

    @Override
    public <T extends Resource> void save(T resource) {
        assert resource != null;

        autoCheckout();

        // Check that it's a resourceURI of our workspace
        if (resource.getURI() == null || !isValidURI(resource.getURI())) {
            throw new RuntimeException("This ResourceManager don't manage the resource '" + resource.getURI() + "'");
        }

        resources.put(resource.getURI(), resource);
        uncommitSaves.add(resource.getURI());

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> List<T> loadChildren(Class<T> resourceType, String parentURI) {

        autoCheckout();

        List<T> children = new ArrayList<T>();

        // Return ROOT projects
        if (Project.class.isAssignableFrom(resourceType) && parentURI == null) {
            for (String projectUri : projectsPaths.keySet()) {

                T resource = load(resourceType, projectUri);

                if (resource != null) {
                    children.add(resource);
                }

            }

            return children;
        }

        for (Resource resource : resources.values()) {
            if (isChild(parentURI, resource.getURI()) && resourceType.isAssignableFrom(resource.getClass())) {
                children.add((T) resource);
            }
        }

        return children;
    }

    @Override
    public void remove(String resourceURI) {

        autoCheckout();

        // Check that it's a resourceURI of our workspace
        if (resourceURI == null || !resources.containsKey(resourceURI)) {
            throw new RuntimeException("This ResourceManager don't manage the resource '" + resourceURI + "'");
        }

        resources.remove(resourceURI);
        uncommitRemoves.add(resourceURI);
        uncommitSaves.remove(resourceURI);
    }

    @SuppressWarnings("rawtypes")
    private static File buildFile(Resource resource, Map.Entry<String, String> projectEntry) {
        for (Class clazz : DEFAULT_CONTAINER_RESOURCES) {
            if (clazz.isAssignableFrom(resource.getClass())) {
                return buildFile(resource, true, projectEntry);
            }
        }

        return buildFile(resource, false, projectEntry);
    }

    private static File buildFile(Resource resource, boolean container, Map.Entry<String, String> projectEntry) {

        String resourceURI = resource.getURI();
        String fileName;

        fileName = resourceURI.replaceFirst(projectEntry.getKey(), "");
        fileName = fileName.replaceAll(String.valueOf(Resource.SEPARATOR), File.separator);

        File containerFolder = null;
        if (container) {
            containerFolder = new File(projectEntry.getValue() + fileName);
            fileName = fileName + File.separator + ONEXUS_CONTAINER_PREFIX
                    + resource.getClass().getSimpleName().toLowerCase() + "." + ONEXUS_FILE_EXTENSION;
        } else {
            int lastSeparator = fileName.lastIndexOf(File.separator);
            if (lastSeparator > 0) {
                containerFolder = new File(projectEntry.getValue() + fileName.substring(0, lastSeparator));
            }
            fileName = fileName + "." + ONEXUS_FILE_EXTENSION;
        }

        if (containerFolder != null && !containerFolder.exists()) {
            containerFolder.mkdirs();
        }

        return new File(projectEntry.getValue() + fileName);

    }

    private static boolean isChild(String parentURI, String resourceURI) {
        if (parentURI == null || resourceURI == null) {
            return false;
        }

        // Trim the two URIs
        parentURI = parentURI.trim();
        resourceURI = resourceURI.trim();

        // End parentURI with a SEPARATOR if it's not present.
        if (parentURI.charAt(parentURI.length() - 1) != Resource.SEPARATOR) {
            parentURI = parentURI + Resource.SEPARATOR;
        }

        if (!resourceURI.startsWith(parentURI)) {
            return false;
        }

        String diff = resourceURI.replaceFirst(parentURI, "");
        if (diff.indexOf(Resource.SEPARATOR) != -1 || diff.isEmpty()) {
            return false;
        }

        return true;
    }

    private static String buildURIFromFile(File file, String projectUri, String projectPath) {
        String path = "";
        String filePath = file.getPath();
        if (projectPath.startsWith(File.separator)) {
            path = filePath.replace(projectPath, "");
        } else {
            path = filePath.substring(filePath.lastIndexOf(projectPath) + projectPath.length());
        }

        path = path.replaceAll(Pattern.quote(File.separator), String.valueOf(Resource.SEPARATOR));

        String resourceURI = projectUri + path;

        if (file.isFile() && file.getName().startsWith(ONEXUS_CONTAINER_PREFIX)) {
            int lastSeparator = resourceURI.lastIndexOf(Resource.SEPARATOR);
            resourceURI = resourceURI.substring(0, lastSeparator);
        } else {
            resourceURI = resourceURI.replace("." + ONEXUS_FILE_EXTENSION, "");
        }

        return resourceURI;
    }

    @Override
    public ResourceStatus status(String resourceURI) {

        autoCheckout();

        if (uncommitRemoves.contains(resourceURI)) {
            return ResourceStatus.REMOVE;
        }

        if (uncommitSaves.contains(resourceURI)) {
            if (uriToAbsolutePath.get(resourceURI) == null) {
                return ResourceStatus.ADD;
            } else {
                return ResourceStatus.UPDATE;
            }
        }

        return ResourceStatus.SYNC;
    }

    @Override
    public void revert(String resourceURI) {

        autoCheckout();

        String resourceFile = uriToAbsolutePath.get(resourceURI);
        Resource resource;
        try {

            Map.Entry<String, String> projectPath = getProjectPathEntry(resourceURI);
            if (resourceFile != null) {
                resource = checkoutFile(new File(resourceFile), projectPath.getKey(), projectPath.getValue());

                if (resource != null) {
                    resources.put(resourceURI, resource);
                }
            } else {
                resources.remove(resourceURI);
            }

            uncommitRemoves.remove(resourceURI);
            uncommitSaves.remove(resourceURI);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
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

    private static Properties loadProperties() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            throw new IllegalStateException("user.home==null");
        }
        File home = new File(userHome);
        File settingsDirectory = new File(home, ".onexus");
        if (!settingsDirectory.exists()) {
            if (!settingsDirectory.mkdir()) {
                throw new IllegalStateException(settingsDirectory.toString());
            }
        }

        File settingsFile = new File(settingsDirectory, "projects.ini");
        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(settingsFile));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return properties;
    }

    private void loadPlugins(Project project) {

        if (project.getPlugins() != null) {
            for (Plugin plugin : project.getPlugins()) {

                String location = plugin.getLocation();

                if (location == null) {
                    LOGGER.error("Plugin " + plugin.getId() + " without location.");
                    continue;
                }

                location = location.trim();

                if (location.isEmpty()) {
                    LOGGER.error("Plugin " + plugin.getId() + " without location.");
                    continue;
                }

                Bundle bundle = null;
                try {
                    bundle = context.installBundle(location, null);
                } catch (IllegalStateException ex) {
                    LOGGER.error(ex.toString());
                } catch (BundleException ex) {
                    if (ex.getNestedException() != null) {
                        LOGGER.error(ex.getNestedException().toString());
                    } else {
                        LOGGER.error(ex.toString());
                    }
                }


                if (bundle != null) {
                    try {
                        bundle.start();
                        LOGGER.debug("Plugin "+plugin.getId()+" installed and started. Bundle ID: " + bundle.getBundleId());
                    } catch (BundleException e) {
                        LOGGER.error("Plugin "+plugin.getId()+" installed but NOT started. Bundle ID: " + bundle.getBundleId(), e);
                    }
                }
            }
        }

    }


}
