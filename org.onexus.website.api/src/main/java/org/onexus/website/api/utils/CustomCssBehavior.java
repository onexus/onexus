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
import org.apache.wicket.util.string.Strings;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.website.api.WebsiteApplication;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class CustomCssBehavior extends Behavior {

    private transient CssHeaderItem CSS;

    private ORI resourceUri;

    @PaxWicketBean(name = "dataManager")
    private IDataManager dataManager;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    public CustomCssBehavior(ORI resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        if (CSS == null) {

            if (resourceUri != null && !Strings.isEmpty(resourceUri.getPath())) {

                String dataService = getDataManager().getMount();
                Project project = getResourceManager().getProject(resourceUri.getProjectUrl());

                String url = WebsiteApplication.toAbsolutePath('/' + dataService + '/' + project.getName() + resourceUri.getPath());
                CSS = CssHeaderItem.forUrl(url);

            } else {
                CSS = CssHeaderItem.forReference(new CssResourceReference(component.getClass(), component.getClass().getSimpleName() + ".css"));
            }
        }

        response.render(CSS);
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
