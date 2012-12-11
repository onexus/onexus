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

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.pages.PageStatus;

import java.io.Serializable;
import java.util.List;

public class WebsiteStatus implements Serializable {

    private String currentPage;

    private List<PageStatus> pageStatuses;

    private transient WebsiteConfig config;

    public WebsiteConfig getConfig() {
        return config;
    }

    public void setConfig(WebsiteConfig config) {
        this.config = config;
    }

    public PageStatus getPageStatus(String id) {
        for (PageStatus status : getPageStatuses()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }

    public void setPageStatus(PageStatus pageStatus) {
        PageStatus oldStatus = getPageStatus(pageStatus.getId());
        pageStatuses.add(pageStatus);
        pageStatuses.remove(oldStatus);
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public List<PageStatus> getPageStatuses() {
        return pageStatuses;
    }

    public void setPageStatuses(List<PageStatus> pageStatuses) {
        this.pageStatuses = pageStatuses;
    }

    public void onQueryBuild(Query query) {
        query.setOn(getConfig().getORI());
    }

    public void encodeParameters(PageParameters parameters) {

        if (pageStatuses != null) {
            parameters.add(Website.PARAMETER_PAGE, currentPage);
            PageStatus status = getPageStatus(currentPage);
            status.encodeParameters(parameters, "p");
        }

    }

    public void decodeParameters(PageParameters parameters) {

        if (pageStatuses != null) {

            StringValue c = parameters.get(Website.PARAMETER_PAGE);
            if (!c.isEmpty()) {
                currentPage = c.toString();
            }

            PageStatus status = getPageStatus(currentPage);
            if (status != null) {
                status.decodeParameters(parameters, "p");
            }
        }

    }


}
