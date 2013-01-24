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
package org.onexus.resource.api;


import java.io.Serializable;
import java.util.regex.Pattern;

public class ORI implements Serializable {

    public static final char SEPARATOR = '/';
    public static final String ONEXUS_TAG = "?";

    private String projectUrl;
    private String path;

    public ORI(String ori) {
        parseString(ori);
    }

    public ORI(ORI parentResource, String relativePath) {
        this.projectUrl = parentResource.projectUrl;
        this.path = resolveRelativePath(parentResource, relativePath);
    }

    public ORI(String projectUrl, String path) {
        this.projectUrl = projectUrl;
        this.path = (path == null || path.isEmpty()) ? null : path;

        if (projectUrl != null && this.path != null && this.path.charAt(0) != SEPARATOR) {
            this.path = '/' + this.path;
        }
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public String getPath() {
        return path;
    }

    public boolean isAbsolute() {
        return projectUrl != null;
    }

    public ORI toAbsolute(ORI parentUri) {

        ORI absoluteOri = new ORI(getProjectUrl(), getPath());

        if (isAbsolute()) {
            return absoluteOri;
        }


        if (path.startsWith(String.valueOf(ORI.SEPARATOR))) {

            // Relative to project URI
            absoluteOri.projectUrl = parentUri.getProjectUrl();
            absoluteOri.path = path;

        } else {

            // Relative URI (../../resorce-name)
            absoluteOri.projectUrl = parentUri.projectUrl;
            absoluteOri.path = resolveRelativePath(parentUri, path);

        }

        return absoluteOri;

    }

    public ORI toRelative(ORI parentUri) {

        ORI relativeOri = new ORI(projectUrl, path);

        // Impossible to make it relative
        if (
                parentUri == null ||
                        projectUrl == null ||
                        !projectUrl.equals(parentUri.projectUrl)
                ) {
            return relativeOri;
        }

        //TODO resolve paths when parentUri is not a direct parent. Like ( /coco/rico ).toRelative( /coco/bueno ) = ../rico
        if (parentUri.path != null && path.startsWith(parentUri.path)) {
            relativeOri.path = path.replace(parentUri.path, "");
        }
        relativeOri.projectUrl = null;
        return relativeOri;

    }

    private static String resolveRelativePath(ORI parent, String relativePath) {

        if (parent == null || relativePath == null) {
            return relativePath;
        }

        ORI container = parent.getParent();
        String relative = ".." + ORI.SEPARATOR;
        while (relativePath.startsWith(relative)) {
            container = container.getParent();
            relativePath = relativePath.substring(3);
        }

        return (container.getPath() == null ? "" : container.getPath()) + SEPARATOR + relativePath;
    }

    public ORI getParent() {
        int sMark = (path == null ? -1 : path.lastIndexOf(ORI.SEPARATOR));
        return new ORI(projectUrl, (sMark < 1 ? null : path.substring(0, sMark)));
    }


    public boolean isChild(ORI childResource) {

        if (childResource == null) {
            return false;
        }

        if (!isAbsolute()) {
            throw new RuntimeException("Impossible to check if '" + childResource + "' is a child of the relative resource '" + toString() + "'");
        }

        if (childResource.path == null) {
            return false;
        }

        if (!projectUrl.equals(childResource.projectUrl)) {
            return false;
        }

        if (path == null) {
            return (childResource.path.lastIndexOf(SEPARATOR) <= 0);
        }

        if (!childResource.path.startsWith(path)) {
            return false;
        }

        if (childResource.path.equals(path)) {
            return false;
        }

        int lSep = childResource.path.lastIndexOf(SEPARATOR);
        return lSep == path.length();
    }

    @Override
    public String toString() {
        if (isAbsolute()) {
            if (path != null) {
                return projectUrl + ONEXUS_TAG + path.substring(1);
            } else {
                return projectUrl.toString();
            }
        } else {
            return path;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ORI)) return false;

        ORI ori = (ORI) o;

        if (projectUrl != null ? !projectUrl.equals(ori.projectUrl) : ori.projectUrl != null) return false;
        if (path != null ? !path.equals(ori.path) : ori.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectUrl != null ? projectUrl.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    private void parseString(String ori) {

        ori = ori.replaceAll("[\n\t ]", "");

        String normalizedOri = ori.trim();

        // Convert Windows separator to linux separator
        normalizedOri = normalizedOri.replaceAll(Pattern.quote("\\"), String.valueOf(SEPARATOR));

        // Remove ending separator if exists
        if (normalizedOri.charAt(normalizedOri.length() - 1) == SEPARATOR) {
            normalizedOri = normalizedOri.substring(0, normalizedOri.length() - 1);
        }

        // Parse the ORI
        int onx = normalizedOri.indexOf(ONEXUS_TAG);
        if (onx != -1) {
            projectUrl = normalizedOri.substring(0, onx);
            path = SEPARATOR + normalizedOri.substring(onx + 1);
        } else {
            if (normalizedOri.contains("://")) {
                projectUrl = normalizedOri;
            } else {
                path = normalizedOri;
            }
        }


    }
}
