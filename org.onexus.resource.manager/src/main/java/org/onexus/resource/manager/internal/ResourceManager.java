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
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.exceptions.UnserializeException;
import org.onexus.core.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.Collection;
import java.util.regex.Pattern;

public class ResourceManager implements IResourceManager {

    @SuppressWarnings("rawtypes")
    public final static Class[] DEFAULT_CONTAINER_RESOURCES = {Workspace.class, Project.class, Folder.class};
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);
    private final static String ONEXUS_CONTAINER_PREFIX = "onexus-";
    private final static String ONEXUS_FILE_EXTENSION = "onx";
    private static final String ONEXUS_WORKSPACE_ENV = "ONEXUS_WORKSPACES";

    // Config properties
    private String baseUrl;
    private String basePath;
    private IResourceSerializer serializer;

    //TODO Make this Session dependent
    private transient boolean checkedout = false;
    private transient Map<String, String> uriToAbsolutePath = new HashMap<String, String>();
    private transient Map<String, String> workspacesPaths = new HashMap<String, String>();
    private transient Map<String, Resource> resources = new HashMap<String, Resource>();
    private transient Set<String> uncommitRemoves = new HashSet<String>();
    private transient Set<String> uncommitSaves = new HashSet<String>();

    public ResourceManager() {
        super();
    }

    public void init() {
        loadWorkspaces();
    }

    private void loadWorkspaces() {
        this.workspacesPaths.clear();

        String envBasePath = System.getenv(ONEXUS_WORKSPACE_ENV);

        if (envBasePath == null) {
            envBasePath = this.basePath;
        }

        File workspacesFolder = new File(envBasePath);

        if (workspacesFolder.isDirectory()) {
            for (File workspaceFolder : workspacesFolder.listFiles()) {

                if (workspaceFolder.isDirectory()) {
                    String wsName = workspaceFolder.getName();
                    this.workspacesPaths.put(this.baseUrl + "/" + wsName, envBasePath + File.separator + wsName);
                }

            }
        }
    }

    private boolean isValidURI(String resourceURI) {
        return getWorkspacePathEntry(resourceURI) != null;
    }

    private Map.Entry<String, String> getWorkspacePathEntry(String resourceURI) {

        if (resourceURI == null) {
            return null;
        }

        for (Map.Entry<String, String> ws : this.workspacesPaths.entrySet()) {
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

        for (Map.Entry<String, String> ws : this.workspacesPaths.entrySet()) {
            File folder = new File(ws.getValue());

            if (!folder.exists()) {
                folder.mkdirs();
            }

            @SuppressWarnings("unchecked")
            Collection<File> files = (Collection<File>) FileUtils.listFiles(folder, null, true);

            for (File file : files) {

                Resource resource = null;

                if (ONEXUS_FILE_EXTENSION.equals(FilenameUtils.getExtension(file.getName()))) {

                    try {
                        resource = checkoutFile(file, ws.getKey(), ws.getValue());
                    } catch (FileNotFoundException e) {
                        LOGGER.error("File '" + file.getPath() + "' not found.");
                        continue;
                    } catch (UnserializeException e) {
                        LOGGER.error("Parsing file " + file.getPath() + " at line " + e.getLine() + " on " + e.getPath());
                        resource = new ResourceFile(file.getPath());
                    } catch (Exception e) {
                        resource = new ResourceFile(file.getPath());
                    }

                } else {
                    resource = new ResourceFile(file.getPath());

                    String resourceURI = buildURIFromFile(file, ws.getKey(), ws.getValue());
                    resource.setURI(resourceURI);

                    String resourceName = FilenameUtils.getName(file.getAbsolutePath());
                    resource.setName(resourceName);
                }


                resources.put(resource.getURI(), resource);
                uriToAbsolutePath.put(resource.getURI(), file.getAbsolutePath());
            }
        }
    }

    private Resource checkoutFile(File file, String workspaceURI, String workspacePath) throws FileNotFoundException {

        Resource resource;
        resource = serializer.unserialize(Resource.class, new FileInputStream(file));

        String resourceURI = buildURIFromFile(file, workspaceURI, workspacePath);
        resource.setURI(resourceURI);

        String resourceName = FilenameUtils.getName(file.getAbsolutePath()).replace("." + ONEXUS_FILE_EXTENSION, "");

        if (resourceName.startsWith(ONEXUS_CONTAINER_PREFIX)) {
            resourceName = FilenameUtils.getName(file.getParent());
        }

        resource.setName(resourceName);

        return resource;
    }

    @Override
    public void commit(String resourceURI) {

        autoCheckout();

        File file = null;
        try {

            if (uncommitSaves.contains(resourceURI)) {
                Resource resource = resources.get(resourceURI);
                file = buildFile(resource, getWorkspacePathEntry(resource.getURI()));

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

        if (parentURI == null && Workspace.class.isAssignableFrom(resourceType)) {
            for (String wsPath : this.workspacesPaths.keySet()) {
                children.add(load(resourceType, wsPath));
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
    private static File buildFile(Resource resource, Map.Entry<String, String> workspaceEntry) {
        for (Class clazz : DEFAULT_CONTAINER_RESOURCES) {
            if (clazz.isAssignableFrom(resource.getClass())) {
                return buildFile(resource, true, workspaceEntry);
            }
        }

        return buildFile(resource, false, workspaceEntry);
    }

    private static File buildFile(Resource resource, boolean container, Map.Entry<String, String> workspaceEntry) {

        String resourceURI = resource.getURI();
        String fileName;

        fileName = resourceURI.replaceFirst(workspaceEntry.getKey(), "");
        fileName = fileName.replaceAll(String.valueOf(Resource.SEPARATOR), File.separator);

        File containerFolder = null;
        if (container) {
            containerFolder = new File(workspaceEntry.getValue() + fileName);
            fileName = fileName + File.separator + ONEXUS_CONTAINER_PREFIX
                    + resource.getClass().getSimpleName().toLowerCase() + "." + ONEXUS_FILE_EXTENSION;
        } else {
            int lastSeparator = fileName.lastIndexOf(File.separator);
            if (lastSeparator > 0) {
                containerFolder = new File(workspaceEntry.getValue() + fileName.substring(0, lastSeparator));
            }
            fileName = fileName + "." + ONEXUS_FILE_EXTENSION;
        }

        if (containerFolder != null && !containerFolder.exists()) {
            containerFolder.mkdirs();
        }

        return new File(workspaceEntry.getValue() + fileName);

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

    private static String buildURIFromFile(File file, String workspaceURI, String workspacePath) {
        String path = "";
        String filePath = file.getPath();
        if (workspacePath.startsWith(File.separator)) {
            path = filePath.replace(workspacePath, "");
        } else {
            path = filePath.substring(filePath.lastIndexOf(workspacePath) + workspacePath.length());
        }

        path = path.replaceAll(Pattern.quote(File.separator), String.valueOf(Resource.SEPARATOR));

        String resourceURI = workspaceURI + path;

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

            Map.Entry<String, String> ws = getWorkspacePathEntry(resourceURI);
            if (resourceFile != null) {
                resource = checkoutFile(new File(resourceFile), ws.getKey(), ws.getValue());

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

}
