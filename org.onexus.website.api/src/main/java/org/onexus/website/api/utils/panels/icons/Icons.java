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
package org.onexus.website.api.utils.panels.icons;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public final class Icons {

    private Icons() {
        super();
    }

    /**
     * Cross icon.
     */
    public static final ResourceReference CROSS = new PackageResourceReference(
            Icons.class, "cross.png");

    /**
     * Question mark icon.
     */
    public static final ResourceReference HELP = new PackageResourceReference(
            Icons.class, "b_help.png");

    /**
     * Thin delete icon.
     */
    public static final ResourceReference THIN_DELETE = new PackageResourceReference(
            Icons.class, "thin_delete.png");

    /**
     * Rounded informative icon (i).
     */
    public static final ResourceReference INFORMATION = new PackageResourceReference(
            Icons.class, "information.png");

    /**
     * Arrow right (usually to uncollapse).
     */
    public static final ResourceReference ARROW_RIGHT = new PackageResourceReference(
            Icons.class, "arrow-right.png");

    /**
     * Arrow right (usually to collapse).
     */
    public static final ResourceReference ARROW_DOWN = new PackageResourceReference(
            Icons.class, "arrow-down.png");

    /**
     * Disk icon
     */
    public static final ResourceReference DISK = new PackageResourceReference(
            Icons.class, "disk.png");

    /**
     * Add icon
     */
    public static final ResourceReference ADDITION = new PackageResourceReference(Icons.class, "addition.png");

    /**
     * Edit page
     */
    public static final ResourceReference EDIT_PAGE = new PackageResourceReference(Icons.class, "page-white_edit.png");


    /**
     * Eye icon
     */
    public static final ResourceReference EYE = new PackageResourceReference(
            Icons.class, "eye.png");


    /**
     * Dark eye icon
     */
    public static final ResourceReference EYE_DARK = new PackageResourceReference(
            Icons.class, "eye-dark.png");

    /**
     * Done mark
     */
    public static final ResourceReference DONE = new PackageResourceReference(
            Icons.class, "done.gif");

    /**
     * Loading icon
     */
    public static final ResourceReference LOADING = new PackageResourceReference(
            Icons.class, "loading.gif");


}
