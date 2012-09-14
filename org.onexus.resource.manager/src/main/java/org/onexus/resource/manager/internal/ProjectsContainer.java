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

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProjectsContainer {

    private static final Logger log = LoggerFactory.getLogger(ProjectsContainer.class);
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

        if (projectUri.startsWith("git://")) {
            return projectImportGit(projectUri);
        }

        return projectImportZipOrFolder(projectUri);
    }

    private File projectImportGit(String projectUri) {

        try {

            File projectFolder = new File(projectsFolder, Integer.toHexString(projectUri.hashCode()));
            if (projectFolder.exists()) {
                throw new InvalidParameterException("This project is already imported");
            }
            projectFolder.mkdir();

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(projectFolder).readEnvironment().findGitDir().build();
            Git git = new Git(repository);
            CloneCommand clone = git.cloneRepository();
            clone.setBare(false);
            clone.setCloneAllBranches(true);
            clone.setDirectory(projectFolder).setURI(projectUri);
            clone.call();

            properties.setProperty(projectUri, projectFolder.getAbsolutePath());
            properties.store(new FileWriter(propertiesFile), "Onexus Projects");

            return projectFolder;

        } catch (Exception e) {
            log.error("Importing project '" + projectUri +"'", e);
            throw new InvalidParameterException("Invalid Onexus URL project");
        }

    }

    private File projectImportZipOrFolder(String projectUri) {
        assert projectUri != null;
        assert !projectUri.isEmpty();

        URL url;
        try {
            url = new URL(projectUri);

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(url.openStream());
            ZipEntry ze = zis.getNextEntry();

            // It's not a ZIP file return it.
            File projectFolder;
            if (ze == null) {
                projectFolder = new File(url.toURI());
                properties.setProperty(projectUri, projectFolder.getAbsolutePath());
                properties.store(new FileWriter(propertiesFile), "Onexus Projects");
                return projectFolder;
            }

            projectFolder = new File(projectsFolder, Integer.toHexString(url.hashCode()));
            if (projectFolder.exists()) {
                throw new InvalidParameterException("This project is already imported");
            }
            projectFolder.mkdir();

            File outputFolder = projectFolder;
            if (ze.isDirectory()) {
                String fileName = ze.getName();
                outputFolder = new File(projectFolder, fileName);
                outputFolder.mkdir();
                ze = zis.getNextEntry();
            }

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(projectFolder, fileName);
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

            properties.setProperty(projectUri, outputFolder.getAbsolutePath());
            properties.store(new FileWriter(propertiesFile), "Onexus Projects");
            return outputFolder;


        } catch (Exception e) {
            log.error("Importing project '" + projectUri +"'", e);
            throw new InvalidParameterException("Invalid Onexus URL project");
        }

    }

}
