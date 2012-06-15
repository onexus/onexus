package org.onexus.resource.manager.internal;

import org.onexus.core.utils.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProjectsContainer {

    private final static String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    private final static String ONEXUS_PROJECTS_SETTINGS = "projects.ini";
    private final static String ONEXUS_PROJECTS_FOLDER = ONEXUS_FOLDER + File.separator + "projects";

    private Properties properties;
    private File projectsFolder;
    private File propertiesFile;

    public ProjectsContainer() {
        super();

        this.properties = new Properties();

        File onexusFolder = new File(ONEXUS_FOLDER);
        projectsFolder = new File(ONEXUS_PROJECTS_FOLDER);
        propertiesFile = new File(onexusFolder, ONEXUS_PROJECTS_SETTINGS);

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

    }

    public File getProjectFolder(String projectUri) {
        return new File(properties.getProperty(projectUri));
    }

    public Collection<String> getProjectUris() {
        return properties.stringPropertyNames();
    }

    public File projectImport(String projectUri) {
        assert projectUri != null;
        assert !projectUri.isEmpty();

        projectUri = ResourceUtils.normalizeUri(projectUri);

        URL url;
        try {
            url = new URL(projectUri);

            File outputFolder = new File(projectsFolder, Integer.toHexString(url.hashCode()));
            if (outputFolder.exists()) {
                throw new InvalidParameterException("This project is already imported");
            }
            outputFolder.mkdir();

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(url.openStream());
            ZipEntry ze = zis.getNextEntry();

            // It's not a ZIP file return it.
            if (ze == null) {
                outputFolder = new File(url.toURI());
                properties.setProperty(projectUri, outputFolder.getAbsolutePath());
                properties.store(new FileWriter(propertiesFile), "Onexus Projects");
                return outputFolder;
            }

            File projectFolder = outputFolder;

            if (ze.isDirectory()) {
                String fileName = ze.getName();
                projectFolder = new File(outputFolder, fileName);
                projectFolder.mkdir();
                ze = zis.getNextEntry();
            }

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder, fileName);
                if (ze.isDirectory()) {
                    newFile.mkdir();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }

                ze = zis.getNextEntry();

            }
            zis.close();

            properties.setProperty(projectUri, projectFolder.getAbsolutePath());
            properties.store(new FileWriter(propertiesFile), "Onexus Projects");
            return projectFolder;


        } catch (Exception e) {
            throw new InvalidParameterException("Invalid Onexus URL project");
        }

    }

}
