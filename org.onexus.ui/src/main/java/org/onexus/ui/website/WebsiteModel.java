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
package org.onexus.ui.website;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetStatus;

import javax.inject.Inject;

public class WebsiteModel implements IModel<WebsiteStatus> {

    private String websiteUri;
    private WebsiteStatus status;

    private transient WebsiteConfig websiteConfig;

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public IResourceSerializer serializer;


    public WebsiteModel(PageParameters pageParameters) {
        super();

        OnexusWebApplication.inject(this);

        init(pageParameters);

    }

    @Override
    public WebsiteStatus getObject() {

        if (status == null) {
            status = getConfig().newStatus();
            setObject(status);
            attachConfigs();
        }

        // Check config is set
        if (status.getConfig() == null) {
            status.setConfig(getConfig());
            attachConfigs();
        }

        return status;
    }

    private WebsiteConfig getConfig() {

        // On attach
        if (websiteConfig == null) {

            // Attach website config
            try {
                websiteConfig = resourceManager.load(WebsiteConfig.class, websiteUri);
            } catch (ClassCastException e) {

                // Force project reload
                resourceManager.syncProject(ResourceUtils.getProjectURI(websiteUri));

                // Try again
                websiteConfig = resourceManager.load(WebsiteConfig.class, websiteUri);
            }

        }

        return websiteConfig;
    }

    private void attachConfigs() {

        // Attach pages
        if (status.getPageStatuses() != null) {
            for (PageStatus pageStatus : status.getPageStatuses()) {
                PageConfig pageConfig = websiteConfig.getPage(pageStatus.getId());
                pageStatus.setConfig(pageConfig);

                // Attach widgets
                if (pageStatus.getWidgetStatuses() != null) {
                    for (Object obj : pageStatus.getWidgetStatuses()) {
                        WidgetStatus widgetStatus = (WidgetStatus) obj;
                        WidgetConfig widgetConfig = pageConfig.getWidget(widgetStatus.getId());
                        widgetStatus.setConfig(widgetConfig);
                    }
                }
            }
        }
    }


    public void setObject(WebsiteStatus object) {
        this.status = object;
    }

    @Override
    public void detach() {
        status.setConfig(null);
    }

    private void init(PageParameters pageParameters) {

        // Load Website config

        StringValue websiteParameter = pageParameters.get(Website.PARAMETER_WEBSITE);
        if (websiteParameter.isEmpty()) {

            // Try to load from Session level
            this.websiteConfig = Session.get().getMetaData(Website.WEBSITE_CONFIG);
            if (this.websiteConfig != null) {
                this.websiteUri = websiteConfig.getURI();
            } else {

                // Try to load from Application level
                this.websiteConfig = Application.get().getMetaData(Website.WEBSITE_CONFIG);

                if (this.websiteConfig != null) {
                    this.websiteUri = websiteConfig.getURI();
                } else {
                    throw new RuntimeException("Unable to find website definition URI");
                }
            }
        } else {
            this.websiteUri = websiteParameter.toString();
        }

        // Force load default config and status
        getObject();

        // Update status
        status.decodeParameters(pageParameters);

    }

}
