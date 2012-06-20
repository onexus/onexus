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

import org.onexus.core.resources.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ResourceUtils {

    private static final char SEPARATOR = Resource.SEPARATOR;
    public static final String ONEXUS_TAG = "?";


    public static String getProjectURI(String resourceURI) {

        if (resourceURI == null) return null;
        int onx = resourceURI.indexOf(ONEXUS_TAG);
        if (onx < 0) return resourceURI;

        return resourceURI.substring(0, onx);
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
        int qMark = resourceURI.lastIndexOf('?');
        int sMark = resourceURI.lastIndexOf(SEPARATOR);
        return resourceURI.substring(0, (qMark > sMark ? qMark : sMark));
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

            properties.put("project.uri", getProjectURI(resourceURI));
            properties.put("project.name", getProjectName(resourceURI));
            properties.put("resource.name", getResourceName(resourceURI));

        }

        return properties;
    }


    public static String concatURIs(String parentURI, String resourceName) {

        char sep = (parentURI.indexOf('?') < 0 ? '?' : Resource.SEPARATOR );

        return parentURI + sep + resourceName;
    }

    public static String getAbsoluteURI(String parentURI, String collectionURI) {

        parentURI = formatURL(parentURI);
        collectionURI = formatURL(collectionURI);

        // Already absolute URI
        if (collectionURI.contains(":/") || parentURI == null
                || parentURI.isEmpty()) {
            return collectionURI;
        }

        // Relative to project URI
        if (collectionURI.startsWith(String.valueOf(Resource.SEPARATOR))) {
            String projectUri = getProjectURI(parentURI);
            return  concatURIs(projectUri, collectionURI.substring(1));
        }

        // Relative URI (../../resorce-name)
        String relative = ".." + Resource.SEPARATOR;
        while (collectionURI.startsWith(relative)) {
            parentURI = getParentURI(parentURI);
            collectionURI = collectionURI.substring(3);
        }

        return concatURIs(parentURI, collectionURI);

    }

    private static String formatURL(String url) {
        return (url == null ? null : url.replaceAll("[\n\t ]", ""));
    }

    public static String normalizeUri(String resourceURI) {

        String normalizedUri = resourceURI.trim();

        // Convert Windows separator to linux separator
        normalizedUri = normalizedUri.replaceAll(Pattern.quote("\\"), String.valueOf(Resource.SEPARATOR));

        // Remove ending separator if exists
        if (normalizedUri.charAt(normalizedUri.length() - 1) == Resource.SEPARATOR) {
            normalizedUri = normalizedUri.substring(0, normalizedUri.length() - 1);
        }

        // Add file protocol if it's empty
        if (normalizedUri.charAt(0) == Resource.SEPARATOR) {
            normalizedUri = "file:" + normalizedUri;
        }

        return normalizedUri;
    }

    public static String getResourcePath(String uri) {

        int qMark = uri.indexOf('?');

        if (qMark < 0) {
            return "";
        }

        return uri.substring(qMark + 1, uri.length());
    }
}
