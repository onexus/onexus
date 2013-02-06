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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.IPageManager;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageModel;
import org.onexus.website.api.theme.DefaultTheme;
import org.onexus.website.api.utils.CustomCssBehavior;
import org.onexus.website.api.utils.HtmlDataResourceModel;
import org.onexus.website.api.utils.authorization.Authorization;
import org.onexus.website.api.utils.panels.ConnectionsPanel;
import org.onexus.website.api.utils.panels.LoginPanel;
import org.onexus.website.api.utils.panels.NotAuthorizedPage;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.ArrayList;
import java.util.List;

public class Website extends WebPage {

    // Parameters
    public final static String PARAMETER_WEBSITE = "ori";
    public final static String PARAMETER_PAGE = "c";

    @PaxWicketBean(name = "pageManager")
    private IPageManager pageManager;

    public Website(PageParameters pageParameters) {
        super(new WebsiteModel(pageParameters));

        if (!Authorization.authorize(getConfig())) {
            if (AuthenticatedWebSession.get().isSignedIn()) {
                setResponsePage(NotAuthorizedPage.class);
            } else {
                WebsiteApplication.get().restartResponseAtSignInPage();
            }
        }

        add(new DefaultTheme());

        final WebsiteStatus status = getStatus();
        final WebsiteConfig config = getConfig();

        ORI parentUri = config.getORI().getParent();
        add(new CustomCssBehavior(parentUri, config.getCss()));

        // Init currentPage
        if (status.getCurrentPage() == null || config.getPage(status.getCurrentPage()) == null) {
            status.setCurrentPage(config.getPages().get(0).getId());
        }

        add(new Label("windowTitle", config.getTitle()));

        add(new EmptyPanel("progressbar"));
        //TODO add(new ProgressBar("progressbar", false));

        String header = config.getHeader();

        Label headerLabel = new Label("header", new HtmlDataResourceModel(parentUri, header));
        headerLabel.setVisible(header != null && !header.isEmpty());
        headerLabel.setEscapeModelStrings(false);
        add(headerLabel);

        WebMarkupContainer menuSection = new WebMarkupContainer("menuSection");
        menuSection.add(new ListView<PageConfig>("menu", new PropertyModel<List<PageConfig>>(this, "pageConfigList" )) {

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
                    link.getParent().add(new AttributeModifier("class", "active" ));
                }

            }
        });

        if (pageParameters.get("embed").toBoolean(false)) {
            menuSection.setVisible(false);
        } else {
            menuSection.setVisible(true);
        }

        // Login section
        Panel login = new LoginPanel("login");
        menuSection.add(login);
        if (config.getLogin() == null || !config.getLogin()) {
            login.setVisible(false);
        }

        // Projects section
        menuSection.add( new ConnectionsPanel("connections", config.getConnections()) );

        add(menuSection);

        String currentPage = status.getCurrentPage();

        add(pageManager.create("page", new PageModel(currentPage, (IModel<WebsiteStatus>) getDefaultModel())));

        String bottom = config.getBottom();
        Label bottomLabel = new Label("bottom", new HtmlDataResourceModel(parentUri, bottom));
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

        if (!Authorization.authorize(getConfig())) {
            if (AuthenticatedWebSession.get().isSignedIn()) {
                setResponsePage(NotAuthorizedPage.class);
            } else {
                WebsiteApplication.get().restartResponseAtSignInPage();
            }
        }

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

            if (Authorization.authorize(page)) {
                pages.add(page);
            }
        }

        return pages;
    }

}
