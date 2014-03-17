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

import org.apache.wicket.markup.html.WebPage;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widget.IWidgetCreator;
import org.onexus.website.api.widget.IWidgetManager;
import org.ops4j.pax.wicket.api.WebApplicationFactory;

public class WebsiteApplicationFactory implements WebApplicationFactory<WebsiteApplication> {

    private String webPath;
    private String website;
    private Class<? extends WebPage> signInPageClass;
    private IWidgetManager pageManager;

    public WebsiteApplicationFactory() {
    }

    public WebsiteApplicationFactory(String webPath, String website, Class<? extends WebPage> signInPageClass, IWidgetManager pageManager) {
        this.webPath = webPath;
        this.website = website;
        this.signInPageClass = signInPageClass;
        this.pageManager = pageManager;
    }


    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Class<? extends WebPage> getSignInPageClass() {
        return signInPageClass;
    }

    public void setSignInPageClass(Class<? extends WebPage> signInPageClass) {
        this.signInPageClass = signInPageClass;
    }

    @Override
    public Class<WebsiteApplication> getWebApplicationClass() {
        return WebsiteApplication.class;
    }

    @Override
    public void onInstantiation(WebsiteApplication application) {
        application.setWebsiteOri(new ORI(website));
        application.setWebPath(webPath);
        application.setSignInPageClass(signInPageClass);

        for (IWidgetCreator pageCreator : pageManager.getCreators()) {
            application.getApplicationListeners().add(pageCreator);
        }
    }
}
