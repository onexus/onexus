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
package org.onexus.website.widgets;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.onexus.resource.api.DefaultResourceRegister;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.IResourceRegister;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.ORI;
import org.onexus.resource.serializer.xstream.internal.ResourceSerializer;
import org.onexus.ui.authentication.jaas.JaasSignInPage;
import org.onexus.website.api.Website;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.widgets.mocks.MockPaxWicketInjector;
import org.onexus.website.widgets.mocks.MockResourceManager;
import org.onexus.website.api.pages.DefaultPageManager;
import org.onexus.website.api.pages.IPageManager;
import org.onexus.website.widgets.pages.browser.BrowserPageCreator;
import org.onexus.website.api.widgets.DefaultWidgetManager;
import org.onexus.website.api.widgets.IWidgetManager;
import org.onexus.website.widgets.text.TextWidgetCreator;
import org.ops4j.pax.wicket.api.InjectorHolder;

import java.util.Arrays;

public class WebsiteTest {

    private WicketTester tester;

    @Before
    public void setUp() {

        // Create beans
        IResourceSerializer resourceSerializer = new ResourceSerializer();
        resourceSerializer.register(WebsiteConfig.class);

        IResourceRegister resourceRegister = new DefaultResourceRegister(resourceSerializer);
        IResourceManager resourceManager = new MockResourceManager();

        IPageManager pageManager = new DefaultPageManager(
                Arrays.asList(
                    new BrowserPageCreator()
                ),
                resourceRegister
        );

        IWidgetManager widgetManager = new DefaultWidgetManager(
                Arrays.asList(
                        new TextWidgetCreator()
                ),
                resourceRegister
        );

        // Create a Website resource
        WebsiteConfig website = resourceSerializer.unserialize(WebsiteConfig.class, getClass().getResourceAsStream("test1.onx"));
        website.setORI(new ORI("http://localhost/test?website"));
        resourceManager.save(website);

        // Create and initialize a Wicket application
        WebsiteApplication application = new WebsiteApplication();
        application.setWebsiteOri(website.getORI());
        application.setWebPath(website.getName());
        application.setSignInPageClass(JaasSignInPage.class);

        tester = new WicketTester(application);

        // Create an injector
        MockPaxWicketInjector injector = new MockPaxWicketInjector();
        injector.addBean(IResourceManager.class, resourceManager);
        injector.addBean(IPageManager.class, pageManager);
        injector.addBean(IWidgetManager.class, widgetManager);

        InjectorHolder.setInjector(application.getApplicationKey(), injector);
        tester.getApplication().getComponentInstantiationListeners().add(injector);

    }

    @Test
    public void renderPage(){
        tester.startPage(new Website(new PageParameters()));
        tester.assertRenderedPage(Website.class);
    }

}
