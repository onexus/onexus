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

import org.onexus.ui.website.IWebsiteModel;
import org.onexus.ui.website.WebsiteStatus;


public class PageModel<S extends PageStatus> implements IPageModel<S> {


    private PageConfig pageConfig;
    private IWebsiteModel websiteModel;
    private S status;

    public PageModel(PageConfig pageConfig) {
        this(pageConfig, null);
    }

    public PageModel(PageConfig pageConfig, IWebsiteModel websiteModel) {
        this.pageConfig = pageConfig;
        this.websiteModel = websiteModel;
    }

    @Override
    public PageConfig getConfig() {
        return pageConfig;
    }

    @Override
    public IWebsiteModel getWebsiteModel() {
        return websiteModel;
    }

    @Override
    public S getObject() {

        if (status != null) {
            return status;
        }

        WebsiteStatus websiteStatus = (websiteModel == null ? null : websiteModel.getObject());
        if (websiteStatus != null) {
            status = (S) websiteStatus.getPageStatus(pageConfig.getId());
        }

        if (status == null) {
            status = (S) pageConfig.getDefaultStatus();

            if (status == null) {
                status = (S) pageConfig.createEmptyStatus();
            }

            if (status != null) {
                setObject(status);
            }
        }

        return status;
    }

    @Override
    public void setObject(S object) {
        this.status = object;

        if (websiteModel != null) {
            WebsiteStatus websiteStatus = websiteModel.getObject();
            if (websiteStatus != null) {
                websiteStatus.setPageStatus(object);
            }
        }

    }

    @Override
    public void detach() {
        if (websiteModel != null) {
            status = null;
        }
    }
}
