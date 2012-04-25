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
package org.onexus.source.manager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.onexus.core.IResourceManager;
import org.onexus.core.ISourceManager;
import org.onexus.core.resources.Source;
import org.onexus.core.utils.ResourceTools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SourceManager implements ISourceManager {

    public final static String ONEXUS_REPOSITORY_ENV = "ONEXUS_REPOSITORY";

    private String repoPath;

    private IResourceManager resourceManager;


    public SourceManager() {
        super();

        repoPath = System.getenv(ONEXUS_REPOSITORY_ENV);

        if (repoPath == null) {
            repoPath = "repository";
        }
    }

    @Override
    public List<URL> retrieve(String sourceURI) {

        Source source = resourceManager.load(Source.class, sourceURI);

        if (source.getRepository() == null) {
            source.setRepository("local");
        }

        if (source.getPaths() == null) {
            source.setPaths(new ArrayList<String>());
        }

        if (source.getPaths().isEmpty()) {
            source.getPaths().add("${workspace.name}/${project.name}/${release.name}/${resource.name}");
        }

        if (!source.getRepository().equals("local")) {
            throw new UnsupportedOperationException("Repository '" + source.getRepository() + "' not supported.");
        }

        List<URL> urls = new ArrayList<URL>();

        for (String templatePath : source.getPaths()) {
            String path = replaceProperties(templatePath, ResourceTools.getProperties(sourceURI));
            String fileName = FilenameUtils.getName(path);

            // Check if it is a wildcard filter
            if (fileName.contains("*") || fileName.contains("?")) {

                // Is recursive?
                IOFileFilter dirFilter = null;
                if (path.contains("**/")) {
                    dirFilter = TrueFileFilter.INSTANCE;
                    path = path.replace("**/", "");
                }

                String sourceContainer = repoPath + File.separator + FilenameUtils.getFullPathNoEndSeparator(path);
                File sourceFile = new File(sourceContainer);

                for (File file : (Collection<File>) FileUtils.listFiles(sourceFile, new WildcardFileFilter(fileName), dirFilter)) {
                    try {
                        urls.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                String sourcePath = repoPath + File.separator + path;
                File sourceFile = new File(sourcePath);

                try {
                    urls.add(sourceFile.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
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
