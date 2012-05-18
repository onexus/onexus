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
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.widgets.bookmark.StatusEncoder;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

public class WebsiteModel implements IModel<WebsiteStatus> {

    private String websiteUri;
    private WebsiteStatus status;

    private transient WebsiteConfig websiteConfig;

    @Inject
    public IResourceManager resourceManager;

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
        }

        // Check config is set
        if (status.getConfig() == null) {
            status.setConfig(getConfig());
        }

        return status;
    }

    private WebsiteConfig getConfig() {

        if (websiteConfig == null) {
            websiteConfig = resourceManager.load(WebsiteConfig.class, websiteUri);
        }

        return websiteConfig;
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

        // Load Website status

        StringValue encodedStatus = pageParameters.get(Website.PARAMETER_STATUS);
        if (!encodedStatus.isEmpty()) {
                try {
                    StatusEncoder statusEncoder = new StatusEncoder(getClass().getClassLoader());
                    status = statusEncoder.decodeStatus(encodedStatus.toString());
                } catch (UnsupportedEncodingException e) {
                    //TODO
                }
        }

        // Set current page

        StringValue pageId = pageParameters.get(Website.PARAMETER_PAGE);
        if (!pageId.isEmpty()) {
            getObject().setCurrentPage(pageId.toString());
        }

    }

}
