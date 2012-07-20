package org.onexus.resource.manager.internal;

import org.apache.commons.io.FilenameUtils;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.exceptions.UnserializeException;
import org.onexus.resource.api.resources.*;
import org.onexus.resource.api.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.Collection;
import java.util.regex.Pattern;

public class ProjectManager {

    private static final Logger log = LoggerFactory.getLogger(ProjectManager.class);
    public final static String ONEXUS_EXTENSION = "onx";
    public final static String ONEXUS_PROJECT_FILE = "onexus-project." + ONEXUS_EXTENSION;

    private IResourceSerializer serializer;
    private String projectUri;
    private File projectFolder;

    private Project project;
    private Map<String, Resource> resources;

    public ProjectManager(IResourceSerializer serializer, String projectUri, File projectFolder) throws InvalidParameterException {
        super();

        this.serializer = serializer;
        this.projectUri = projectUri;
        this.projectFolder = projectFolder;

    }

    public Project getProject() {
        if (project == null) {
            loadProject();
        }

        return project;
    }

    public void loadResources() {

        this.resources = new HashMap<String, Resource>();

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

    public Resource getResource(String resourceUri) {

        if (projectUri.equals(resourceUri)) {
            return project;
        }

        if (resources == null) {
            loadResources();
        }

        return resources.get(resourceUri);

    }

    public <T extends Resource> List<T> getResourceChildren(Class<T> resourceType, String parentURI) {

        String projectURI = ResourceUtils.getProjectURI(parentURI);

        if (projectURI.equals(parentURI)) {
            parentURI = projectURI + "?";
        } else {
            // End parentURI with a SEPARATOR if it's not present.
            if (parentURI.charAt(parentURI.length() - 1) != Resource.SEPARATOR) {
                parentURI = parentURI + Resource.SEPARATOR;
            }
        }

        if (resources == null) {
            loadResources();
        }

        List<T> children = new ArrayList<T>();
        for (Resource resource : resources.values()) {
            if (isChild(parentURI, resource.getURI()) && resourceType.isAssignableFrom(resource.getClass())) {
                children.add((T) resource);
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

        if (project.getRepositories() == null) {
            project.setRepositories(new ArrayList<Repository>());
        }

        for (Repository repository : project.getRepositories()) {
            if (repository.getId().equals("resource")) {
                repository.setLocation(projectFolder.getAbsolutePath());
                return;
            }
        }

        project.getRepositories().add(new Repository("resource", projectFolder.getAbsolutePath()));

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

        String resourceURI;
        if (relativePath.equals(ONEXUS_PROJECT_FILE)) {
            resourceURI = projectUri;
            if (resource.getName() == null) {
                resource.setName(ResourceUtils.getResourceName(projectUri));
            }
        } else {
            resourceURI = projectUri + "?" + relativePath;
            resourceURI = resourceURI.replace("." + ONEXUS_EXTENSION, "");
            resource.setName(resourceName);
        }

        resource.setURI(resourceURI);
        return resource;

    }

    private Resource createErrorResource(File resourceFile, String msg) {
        log.error(msg);
        Resource errorResource = createDataResource(resourceFile);
        errorResource.setDescription("ERROR: " + msg);
        return errorResource;
    }

    private Resource createDataResource(File resourceFile) {
        String repositoryPath = resourceFile.getPath().replaceFirst(Pattern.quote(projectFolder.getPath() + File.separator), "");
        return new Data("resource", repositoryPath);
    }

    private static boolean isChild(String parentURI, String resourceURI) {
        if (parentURI == null || resourceURI == null) {
            return false;
        }

        // Trim the two URIs
        parentURI = parentURI.trim();
        resourceURI = resourceURI.trim();

        if (!resourceURI.startsWith(parentURI)) {
            return false;
        }

        String diff = resourceURI.replace(parentURI, "");
        if (diff.indexOf(Resource.SEPARATOR) != -1 || diff.isEmpty()) {
            return false;
        }

        return true;
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
            throw new IllegalArgumentException("Cannot create a project '" + resource.getURI() + "' inside project '" + projectUri + "'");
        }

        String filePath = ResourceUtils.getResourcePath(resource.getURI());

        File file;
        if (resource instanceof Folder) {
            file = new File(projectFolder, filePath);
            file.mkdirs();

            return;
        }


        file = new File(projectFolder, filePath + "." + ONEXUS_EXTENSION);

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
