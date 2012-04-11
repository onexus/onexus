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
package org.onexus.core.utils;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceTools {

    private static final char SEPARATOR = Resource.SEPARATOR;
    public static final String ONEXUS_TAG = "onx";


    public static String getServerURI(String resourceURI) {
        return resourceURI.substring(0, resourceURI.indexOf(ONEXUS_TAG)+3);
    }

    public static String getWorkspaceURI(String resourceURI) {
        int onx = resourceURI.indexOf(ONEXUS_TAG);
        int sep1 = resourceURI.indexOf(SEPARATOR, onx+4);
        return resourceURI.substring(0, sep1);
    }

    public static String getWorkspaceName(String resourceURI) {
        return getResourceName(getWorkspaceURI(resourceURI));
    }

    public static String getProjectURI(String resourceURI) {
        int onx = resourceURI.indexOf(ONEXUS_TAG);
        int sep1 = resourceURI.indexOf(SEPARATOR, onx+4);
        int sep2 = resourceURI.indexOf(SEPARATOR, sep1+1);
        return resourceURI.substring(0, sep2);
    }

    public static String getProjectName(String resourceURI) {
        return getResourceName(getProjectURI(resourceURI));
    }

    public static String getReleaseURI(String resourceURI) {
        int onx = resourceURI.indexOf(ONEXUS_TAG);
        int sep1 = resourceURI.indexOf(SEPARATOR, onx+4);
        int sep2 = resourceURI.indexOf(SEPARATOR, sep1+1);
        int sep3 = resourceURI.indexOf(SEPARATOR, sep2+1);
        return resourceURI.substring(0, sep3);
    }

    public static String getReleaseName(String resourceURI) {
        return getResourceName(getReleaseURI(resourceURI));
    }

    public static String getReleasePath(String resourceURI) {
        String path = resourceURI.replace(getReleaseURI(resourceURI), "").replace(getResourceName(resourceURI), "");

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }

        return path;
    }

    public static String getParentURI(String resourceURI) {
        return resourceURI.substring(0, resourceURI.lastIndexOf(SEPARATOR));
    }

    public static String getResourceName(String resourceURI) {
        return resourceURI.substring(resourceURI.lastIndexOf(SEPARATOR) + 1);
    }

    public static Map<String, String> getProperties(String resourceURI) {

        Map<String, String> properties = new HashMap<String, String>();

        properties.put("server.uri", getServerURI(resourceURI));
        properties.put("workspace.uri", getWorkspaceURI(resourceURI));
        properties.put("workspace.name", getWorkspaceName(resourceURI));
        properties.put("project.uri", getProjectURI(resourceURI));
        properties.put("project.name", getProjectName(resourceURI));
        properties.put("release.uri", getReleaseURI(resourceURI));
        properties.put("release.name", getReleaseName(resourceURI));
        properties.put("release.path", getReleasePath(resourceURI));
        properties.put("resource.name", getResourceName(resourceURI));

        return properties;
    }


    public static String concatURIs(String parentURI, String resourceName) {
        return parentURI + Resource.SEPARATOR + resourceName;
    }

    public static String getAbsoluteURI(String releaseURI, String collectionURI) {

        releaseURI = formatURL(releaseURI);
        collectionURI = formatURL(collectionURI);

        // Already absolute URI
        if (collectionURI.contains("://") || releaseURI == null
                || releaseURI.isEmpty()) {
            return collectionURI;
        }

        // Relative URI
        return concatURIs(releaseURI, collectionURI);
    }

    private static String formatURL(String url) {
        return (url == null ? null : url.replaceAll("[\n\t ]", ""));
    }

}
