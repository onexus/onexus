package org.onexus.website.api;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.onexus.resource.api.*;
import org.onexus.resource.serializer.xstream.internal.ResourceSerializer;
import org.onexus.ui.authentication.jaas.JaasSignInPage;
import org.onexus.website.api.mocks.MockPaxWicketInjector;
import org.onexus.website.api.mocks.MockResourceManager;
import org.onexus.website.api.pages.DefaultPageManager;
import org.onexus.website.api.pages.IPageManager;
import org.onexus.website.api.pages.browser.BrowserPageCreator;
import org.onexus.website.api.widgets.DefaultWidgetManager;
import org.onexus.website.api.widgets.IWidgetManager;
import org.onexus.website.api.widgets.text.TextWidgetCreator;
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
        //tester.dumpPage();
    }

}
