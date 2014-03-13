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
package org.onexus.website.api;

import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.resource.api.utils.ResourceListener;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetStatus;

import javax.inject.Inject;

public class WebsiteModel implements IModel<WebsiteStatus> {

    private WebsiteStatus status;

    private transient WebsiteConfig websiteConfig;

    @Inject
    private IResourceManager resourceManager;

    public WebsiteModel(PageParameters pageParameters) {
        super();

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

            ORI websiteUri = WebsiteApplication.get().getWebsiteOri();

            // Attach website config
            LoginContext ctx = LoginContext.get();
            try {
                LoginContext.set(LoginContext.SERVICE_CONTEXT, null);
                websiteConfig = getResourceManager().load(WebsiteConfig.class, websiteUri);
            } catch (ClassCastException e) {

                // Force project reload
                getResourceManager().syncProject(websiteUri.getProjectUrl());

                // Try again
                websiteConfig = getResourceManager().load(WebsiteConfig.class, websiteUri);
            } finally {
                LoginContext.set(ctx, null);
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

        // Force load default config and status
        getObject();

        // Update status
        status.decodeParameters(pageParameters);

    }

    private IResourceManager getResourceManager() {

        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }

}
