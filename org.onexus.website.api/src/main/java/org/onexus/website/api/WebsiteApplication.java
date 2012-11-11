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

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.resource.api.ORI;
import org.ops4j.pax.wicket.api.InjectorHolder;

import javax.servlet.http.HttpServletRequest;

public class WebsiteApplication extends AuthenticatedWebApplication  {

    private final static String SERVER_URL = System.getenv("ONEXUS_SERVER_URL");

    private String webPath;
    private ORI websiteOri;

    public WebsiteApplication() {
        super();
    }

    public Class<? extends Page> getHomePage() {
        return Website.class;
    }

    @Override
    protected void init() {
        super.init();
        mountPage(webPath + "/${"+Website.PARAMETER_PAGE+"}/#{ptab}", Website.class);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return WebsiteSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    public static WebsiteApplication get() {
        return (WebsiteApplication) Application.get();
    }

    @Deprecated
    public String getWebserviceUrl() {
        return "http://localhost:8181/onexus/onx";
    }

    public ORI getWebsiteOri() {
        return websiteOri;
    }

    public void setWebsiteOri(ORI websiteOri) {
        this.websiteOri = websiteOri;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getRequestUrl() {

        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        String requestUrl = request.getRequestURL().toString();

        if (SERVER_URL != null) {
            String baseUrl = RequestCycle.get().getUrlRenderer().getBaseUrl().toString();
            return SERVER_URL + '/' + baseUrl;
        }

        return requestUrl;
    }

    public static void inject(Object obj) {
        if (obj != null) {
            InjectorHolder.getInjector().inject(obj, obj.getClass());
        }
    }

}
