/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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

public class ResourceTools {

    public static String getParentURI(String resourceURI) {
        return resourceURI.substring(0,
                resourceURI.lastIndexOf(Resource.SEPARATOR));
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

    public static String getResourceName(String resourceURI) {
        return resourceURI.substring(resourceURI.lastIndexOf(Resource.SEPARATOR) + 1);
    }

}
