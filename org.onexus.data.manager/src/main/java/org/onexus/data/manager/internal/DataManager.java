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
package org.onexus.data.manager.internal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.onexus.core.IDataManager;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Data;
import org.onexus.core.resources.Project;
import org.onexus.core.resources.Repository;
import org.onexus.core.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class DataManager implements IDataManager {

    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private IResourceManager resourceManager;

    public DataManager() {
        super();
    }

    @Override
    public List<URL> retrieve(String dataURI) {

        Data data = resourceManager.load(Data.class, dataURI);

        if (data.getPaths() == null) {
            data.setPaths(new ArrayList<String>());
        }

        String projectURI = ResourceUtils.getProjectURI(dataURI);
        Project project = resourceManager.load(Project.class, projectURI);
        Repository repository = null;
        if (data.getRepository() == null) {
            if (!project.getRepositories().isEmpty()) {
                repository = project.getRepositories().get(0);
            }
        } else {
            for (Repository r : project.getRepositories()) {
                if (data.getRepository().equals(r.getId())) {
                    repository = r;
                    break;
                }
            }
        }


        if (repository == null) {
            throw new UnsupportedOperationException("Repository '" + data.getRepository() + "' not defined in project " + projectURI);
        }

        List<URL> urls = new ArrayList<URL>();

        for (String templatePath : data.getPaths()) {
            String path = replaceProperties(templatePath, ResourceUtils.getProperties(dataURI));
            String fileName = FilenameUtils.getName(path);

            // Check if it is a wildcard filter
            if (fileName.contains("*") || fileName.contains("?")) {

                // Is recursive?
                IOFileFilter dirFilter = null;
                if (path.contains("**/")) {
                    dirFilter = TrueFileFilter.INSTANCE;
                    path = path.replace("**/", "");
                }

                String sourceContainer = repository.getLocation() + File.separator + FilenameUtils.getFullPathNoEndSeparator(path);
                File sourceFile = new File(sourceContainer);

                for (File file : (Collection<File>) FileUtils.listFiles(sourceFile, new WildcardFileFilter(fileName), dirFilter)) {
                    try {
                        urls.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                String sourcePath = repository.getLocation() + File.separator + path;
                File sourceFile = new File(sourcePath);

                if (sourceFile.exists()) {
                    try {
                        urls.add(sourceFile.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                } else {

                    // Try mirrors
                    Random rand = new Random();
                    if (repository.getMirrors() != null && !repository.getMirrors().isEmpty()) {
                        int randomMirror = rand.nextInt(repository.getMirrors().size());

                        String mirror = repository.getMirrors().get(randomMirror);
                        String remoteFile = mirror + '/' + path;

                        try {
                            URL url = new URL(remoteFile);
                            urls.add(url);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        }

        return urls;
    }

    private String replaceProperties(String strUrl, Map<String, String> properties) {

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            strUrl = strUrl.replaceAll(Pattern.quote("${" + entry.getKey() + "}"), entry.getValue());
        }

        return strUrl;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
