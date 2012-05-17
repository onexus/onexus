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
package org.onexus.ui.website.pages;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.WebsiteStatus;


public class PageModel<S extends PageStatus> extends AbstractWrapModel<S> {

    private String pageId;
    private IModel<? extends WebsiteStatus> websiteModel;

    public PageModel(String pageId, IModel<? extends WebsiteStatus> websiteModel) {
        super();

        this.pageId = pageId;
        this.websiteModel = websiteModel;
    }

    @Override
    public S getObject() {

        S status = (S) getWebsiteStatus().getPageStatus(pageId);

        if (status == null) {
            status = (S) getConfig().newStatus();
            setObject(status);
        }

        // Check config is set
        if (status.getConfig() == null) {
            status.setConfig(getConfig());
        }

        return status;
    }

    private WebsiteStatus getWebsiteStatus() {
        return websiteModel.getObject();
    }

    private PageConfig getConfig() {

        PageConfig config = getWebsiteStatus().getConfig().getPage(pageId);

        // Check pageConfig is set
        if (config.getWebsiteConfig() == null) {
            config.setWebsiteConfig(getWebsiteStatus().getConfig());
        }

        return config;
    }

    @Override
    public void setObject(S object) {
        websiteModel.getObject().setPageStatus(object);
    }

    @Override
    public IModel<?> getWrappedModel() {
        return websiteModel;
    }
}
