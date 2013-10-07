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
package org.onexus.ui.website.internal.workspace;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.website.api.IWebsiteService;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class WebsiteViewer extends Panel {

    @PaxWicketBean(name = "websiteService")
    private IWebsiteService websiteService;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    public WebsiteViewer(String id, final IModel<? extends Resource> model) {
        super(id);

        WebsiteConfig website = (WebsiteConfig) model.getObject();
        Project project = resourceManager.getProject(website.getORI().getProjectUrl());

        String path = '/' + websiteService.getMount() + '/' + project.getName() + '/' + website.getName() + '/';
        String src = WebsiteApplication.toAbsolutePath(path);

        WebMarkupContainer iframe = new WebMarkupContainer("browser");
        iframe.add(new AttributeModifier("src", src));

        add(iframe);
    }

}
