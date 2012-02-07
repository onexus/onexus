/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WicketFilter;
import org.wicketstuff.osgi.OsgiClassResolver;
import org.wicketstuff.osgi.inject.OsgiComponentInjector;

public abstract class OnexusWebApplication extends AuthenticatedWebApplication {
    
    // Force to import the package
    public final static WicketFilter wicketFilter = null;
    
    @Override
    protected void init() {
	super.init();
	mountPage("/login", getSignInPageClass());
	getApplicationSettings().setAccessDeniedPage(getSignInPageClass());
	
	getComponentInstantiationListeners().add(new OsgiComponentInjector());
	getSessionListeners().add(new OsgiSessionInjector());
	getApplicationListeners().add(new OsgiApplicationInjector());
	getApplicationSettings().setClassResolver(new OsgiClassResolver());
    }
    
    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
	return OnexusSignInPage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
	return OnexusWebSession.class;
    }
    
    public static OnexusWebApplication get() {
	return (OnexusWebApplication) Application.get();
    }
    
}
