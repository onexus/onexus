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
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.utils.error.ExceptionErrorPage;
import org.ops4j.pax.wicket.api.InjectorHolder;

import javax.servlet.http.HttpServletRequest;

public class WebsiteApplication extends AuthenticatedWebApplication  {

    private final static String SERVER_URL = System.getProperty("onexus.server.url");

    private String webPath;
    private ORI websiteOri;

    public WebsiteApplication() {
        super();
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    public Class<? extends Page> getHomePage() {
        return Website.class;
    }

    @Override
    protected void init() {
        super.init();
        mountPage(webPath + "/${" + Website.PARAMETER_PAGE + "}/#{ptab}", Website.class);

        /* In case of unhandled exception redirect it to a custom page */
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception e) {
                return new RenderPageRequestHandler(new PageProvider(new ExceptionErrorPage(e)));
            }
        });
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

    public final static String toAbsolutePath(String relativePagePath) {
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        String serverUrl = SERVER_URL;
        if (serverUrl == null) {
            serverUrl = "http://" + request.getServerName() + ":" + request.getServerPort();
        }

        if (relativePagePath.charAt(0) == '/') {
            return serverUrl + relativePagePath;
        }

        return RequestUtils.toAbsolutePath(request.getRequestURL().toString(), relativePagePath.toString());
    }

    public static void inject(Object obj) {
        if (obj != null) {
            InjectorHolder.getInjector().inject(obj, obj.getClass());
        }
    }

}
