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

public class ResourceUtils {

    private static final char SEPARATOR = Resource.SEPARATOR;
    public static final String ONEXUS_TAG = "onx";


    public static String getServerURI(String resourceURI) {

        if (resourceURI == null) return null;
        int onx = resourceURI.indexOf(ONEXUS_TAG);
        if (onx < 0) return null;

        return resourceURI.substring(0, onx + 3);
    }

    public static String getProjectURI(String resourceURI) {

        int onx = resourceURI.indexOf(ONEXUS_TAG);
        if (onx < 0) return null;

        int sep1 = resourceURI.indexOf(SEPARATOR, onx + 4);
        if (sep1 < 0) {
            sep1 = resourceURI.length();
        }

        return resourceURI.substring(0, sep1);
    }

    public static String getProjectName(String resourceURI) {
        return getResourceName(getProjectURI(resourceURI));
    }

    public static String getRelativeURI(String parentURI, String resourceURI) {

        if (parentURI == null) {
            return resourceURI;
        }

        if (resourceURI == null) {
            return parentURI;
        }

        if (!resourceURI.startsWith(parentURI + Resource.SEPARATOR)) {
            return resourceURI;
        }

        return resourceURI.replace(parentURI + Resource.SEPARATOR, "");
    }

    public static String getParentURI(String resourceURI) {
        return resourceURI.substring(0, resourceURI.lastIndexOf(SEPARATOR));
    }

    public static String getResourceName(String resourceURI) {

        if (resourceURI == null) {
            return null;
        }

        return resourceURI.substring(resourceURI.lastIndexOf(SEPARATOR) + 1);
    }

    public static Map<String, String> getProperties(String resourceURI) {

        Map<String, String> properties = new HashMap<String, String>();

        if (resourceURI != null) {

            properties.put("server.uri", getServerURI(resourceURI));
            properties.put("project.uri", getProjectURI(resourceURI));
            properties.put("project.name", getProjectName(resourceURI));
            properties.put("resource.name", getResourceName(resourceURI));

        }

        return properties;
    }


    public static String concatURIs(String parentURI, String resourceName) {
        return parentURI + Resource.SEPARATOR + resourceName;
    }

    public static String getAbsoluteURI(String parentURI, String collectionURI) {

        parentURI = formatURL(parentURI);
        collectionURI = formatURL(collectionURI);

        // Already absolute URI
        if (collectionURI.contains("://") || parentURI == null
                || parentURI.isEmpty()) {
            return collectionURI;
        }

        // Relative to project URI
        if (collectionURI.charAt(0) == Resource.SEPARATOR) {
            return getProjectURI(parentURI) + collectionURI;
        }

        // Relative URI (../../resorce-name)
        String relative = ".." + Resource.SEPARATOR;
        while (collectionURI.startsWith(relative)) {
            parentURI = getParentURI(parentURI);
            collectionURI = collectionURI.substring(3);
        }

        // Same parent URI
        return concatURIs(parentURI, collectionURI);
    }

    private static String formatURL(String url) {
        return (url == null ? null : url.replaceAll("[\n\t ]", ""));
    }

}
