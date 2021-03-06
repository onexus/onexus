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
package org.onexus.website.api.utils;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResource;
import org.apache.wicket.util.string.Strings;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.website.api.WebsiteApplication;

import javax.inject.Inject;

public class CustomCssBehavior extends Behavior {

    private transient CssHeaderItem cssHeaderItem;

    private ORI resourceUri;

    @Inject
    private IDataManager dataManager;

    @Inject
    private IResourceManager resourceManager;

    public CustomCssBehavior(ORI parentOri, String cssTag) {
        if (!Strings.isEmpty(cssTag)) {
            resourceUri = new ORI(cssTag);
            if (!resourceUri.isAbsolute()) {
                resourceUri = resourceUri.toAbsolute(parentOri);
            }
        }
    }

    private CustomCssBehavior(ORI resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        if (cssHeaderItem == null) {

            if (resourceUri != null) {

                String dataService = getDataManager().getMount();
                Project project = getResourceManager().getProject(resourceUri.getProjectUrl());

                String url;
                if (project != null) {
                    url = WebsiteApplication.toAbsolutePath('/' + dataService + '/' + project.getName() + resourceUri.getPath());
                } else {
                    url = resourceUri.toString();
                }
                cssHeaderItem = CssHeaderItem.forUrl(url);
                response.render(cssHeaderItem);

            } else {
                Class scope = component.getClass();
                String name = scope.getSimpleName() + ".css";
                if (PackageResource.exists(scope, name, null, null, null)) {
                    cssHeaderItem = CssHeaderItem.forReference(new CssResourceReference(scope, name));
                    response.render(cssHeaderItem);
                }
            }

        } else {

            response.render(cssHeaderItem);

        }
    }

    private IDataManager getDataManager() {

        if (dataManager == null) {
            WebsiteApplication.inject(this);
        }

        return dataManager;
    }

    private IResourceManager getResourceManager() {

        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }

}
