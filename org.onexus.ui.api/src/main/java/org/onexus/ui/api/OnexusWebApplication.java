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
package org.onexus.ui.api;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.ui.api.pages.SignOutPage;
import org.onexus.ui.api.pages.error.ExceptionErrorPage;
import org.onexus.ui.api.pages.resource.ResourcesPage;
import org.onexus.ui.authentication.jaas.JaasSignInPage;
import org.onexus.ui.authentication.persona.PersonaSignInPage;
import org.ops4j.pax.wicket.api.InjectorHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class OnexusWebApplication extends AuthenticatedWebApplication {

    private static final String SERVER_URL = System.getenv("ONEXUS_SERVER_URL");

    public Class<? extends Page> getHomePage() {
        return ResourcesPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    @Override
    protected void init() {
        super.init();

        // Authentication
        getApplicationSettings().setAccessDeniedPage(getSignInPageClass());

        /* In case of unhandled exception redirect it to a custom page */
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception e) {
                return new RenderPageRequestHandler(new PageProvider(new ExceptionErrorPage(e)));
            }
        });

        // Set the login context on each thread
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                HttpServletRequest webRequest = (HttpServletRequest) cycle.getRequest().getContainerRequest();
                HttpSession session = webRequest.getSession();

                if (session != null && LoginContext.get(session.getId()) != null) {
                    LoginContext.set(LoginContext.get(session.getId()), null);
                } else {
                    LoginContext.set(LoginContext.ANONYMOUS_CONTEXT, null);
                }
            }
        });

        // Mount pages
        mountPage("/login", getSignInPageClass());
        mountPage("/logout", SignOutPage.class);
        mountPage("/admin", ResourcesPage.class);

    }

    public boolean usePersonSignIn() {
        return Boolean.parseBoolean(System.getProperty("org.onexus.ui.authentication.persona", "false"));
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        if (usePersonSignIn()) {
            return PersonaSignInPage.class;
        }

        return JaasSignInPage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return OnexusWebSession.class;
    }

    public static OnexusWebApplication get() {
        return (OnexusWebApplication) Application.get();
    }

    public static String getRequestUrl() {

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
