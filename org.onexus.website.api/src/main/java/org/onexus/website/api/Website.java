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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.IPageManager;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageModel;
import org.onexus.website.api.theme.DefaultTheme;
import org.onexus.website.api.utils.CustomCssBehavior;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.ArrayList;
import java.util.List;

public class Website extends WebPage {

    // Parameters
    public final static String PARAMETER_WEBSITE = "uri";
    public final static String PARAMETER_PAGE = "c";

    @PaxWicketBean(name = "pageManager")
    private IPageManager pageManager;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    public Website(PageParameters pageParameters) {
        super(new WebsiteModel(pageParameters));

        add(new DefaultTheme());

        final WebsiteStatus status = getStatus();
        final WebsiteConfig config = getConfig();

        ORI parentUri = config.getURI().getParent();
        ORI cssUri = new ORI(parentUri, config.getCss());
        add(new CustomCssBehavior(cssUri));

        // Init currentPage
        if (status.getCurrentPage() == null) {
            status.setCurrentPage(config.getPages().get(0).getId());
        }

        add(new Label("windowTitle", config.getTitle()));

        add(new EmptyPanel("progressbar"));
        //TODO add(new ProgressBar("progressbar", false));

        String header = config.getHeader();
        ORI headerUri = new ORI(parentUri, header);

        Label headerLabel = new Label("header", new HtmlDataResourceModel(headerUri, this));
        headerLabel.setVisible(header != null && !header.isEmpty());
        headerLabel.setEscapeModelStrings(false);
        add(headerLabel);

        WebMarkupContainer menuSection = new WebMarkupContainer("menuSection");
        menuSection.add(new ListView<PageConfig>("menu", new PropertyModel<List<PageConfig>>(this, "pageConfigList")) {

            @Override
            protected void populateItem(ListItem<PageConfig> item) {

                PageConfig pageConfig = item.getModelObject();

                PageParameters parameters = new PageParameters();
                parameters.add(PARAMETER_PAGE, pageConfig.getId());

                Link<String> link = new BookmarkablePageLink<String>("link", Website.class, parameters);
                link.add(new Label("name", pageConfig.getLabel()));

                String currentPage = status.getCurrentPage();

                item.add(link);

                if (currentPage.equals(pageConfig.getId())) {
                    link.getParent().add(new AttributeModifier("class", "active"));
                }

            }
        });

        if (pageParameters.get("embed").toBoolean(false)) {
            menuSection.setVisible(false);
        } else {
            menuSection.setVisible(true);
        }


        // Login section
        //TODO final boolean isAnonymous = AuthenticatedWebSession.get().getRoles().isEmpty();
        final boolean isAnonymous = true;

        Link<String> link = new Link<String>("account-details") {
            @Override
            public void onClick() {

                if (isAnonymous) {
                    //TODO WebsiteSession.get().signOut();
                    setResponsePage(Website.this);
                    //TODO WebsiteApplication.get().restartResponseAtSignInPage();
                }

            }
        };
        link.add(new AttributeModifier("title", (isAnonymous ? "Sign in" : "Account Details")));
        //TODO link.add(new Label("username", (isAnonymous?"Sign in" : OnexusWebSession.get().getUserToken())));
        link.add(new Label("username", (isAnonymous ? "Sign in" : "TODO")));

        Link<String> signOut = new Link<String>("signout") {
            @Override
            public void onClick() {
                //TODO OnexusWebSession.get().invalidate();
            }
        };

        link.setVisible(!isAnonymous);
        signOut.setVisible(!isAnonymous);

        menuSection.add(signOut);
        menuSection.add(link);

        add(menuSection);

        String currentPage = status.getCurrentPage();

        add(pageManager.create("page", new PageModel(currentPage, (IModel<WebsiteStatus>) getDefaultModel())));

        if (config != null && config.getAuthorization() != null) {

            String role = config.getAuthorization();

            if (!AuthenticatedWebSession.get().getRoles().hasRole(role)) {
                //TODO OnexusWebApplication.get().restartResponseAtSignInPage();
            }

        }

        String bottom = config.getBottom();
        ORI bottomUri = new ORI(parentUri, bottom);

        Label bottomLabel = new Label("bottom", new HtmlDataResourceModel(bottomUri, this));
        bottomLabel.setVisible(bottom != null && !bottom.isEmpty());
        bottomLabel.setEscapeModelStrings(false);
        add(bottomLabel);


    }

    @Override
    protected void onBeforeRender() {
        StringValue embed = getPage().getPageParameters().get("embed");

        boolean visible = !embed.toBoolean(false);

        get("header").setVisible(visible);
        get("bottom").setVisible(visible);

        super.onBeforeRender();
    }

    public WebsiteStatus getStatus() {
        return (WebsiteStatus) getDefaultModelObject();
    }

    public WebsiteConfig getConfig() {
        return getStatus().getConfig();
    }

    public List<PageConfig> getPageConfigList() {
        List<PageConfig> pages = new ArrayList<PageConfig>();

        for (PageConfig page : getConfig().getPages()) {

            String role = page.getAuthorization();

            if (role != null && !role.isEmpty()) {
                if (role.equalsIgnoreCase("anonymous")) {
                    if (!AuthenticatedWebSession.get().getRoles().isEmpty()) {
                        continue;
                    }
                } else if (!AuthenticatedWebSession.get().getRoles().hasRole(role)) {
                    continue;
                }
            }

            pages.add(page);
        }

        return pages;
    }

}
