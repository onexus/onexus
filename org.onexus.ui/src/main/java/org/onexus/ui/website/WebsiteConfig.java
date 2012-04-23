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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.core.resources.Resource;
import org.onexus.ui.website.pages.PageConfig;

import java.util.List;

@XStreamAlias("website")
public class WebsiteConfig extends Resource {

    private WebsiteStatus defaultStatus;

    private String authorization;

    private List<PageConfig> pages;

    public WebsiteConfig() {
        super();
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public WebsiteStatus getDefault() {
        return defaultStatus;
    }

    public WebsiteStatus createEmptyStatus() {
        return new WebsiteStatus();
    }

    public void setDefault(WebsiteStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public List<PageConfig> getPages() {
        return pages;
    }

    public PageConfig getPage(String pageId) {
        if (pages != null) {
            for (PageConfig page : pages) {
                if (pageId.equals(page.getId())) {
                    return page;
                }
            }
        }

        return null;
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages;
    }

}