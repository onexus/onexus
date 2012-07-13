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

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
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
import org.onexus.core.IDataManager;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.IPageManager;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageModel;
import org.onexus.ui.website.theme.DefaultTheme;
import org.onexus.ui.workspace.progressbar.ProgressBar;

import javax.inject.Inject;
import java.util.List;

public class Website extends WebPage {

    public final static MetaDataKey<WebsiteConfig> WEBSITE_CONFIG = new MetaDataKey<WebsiteConfig>() {
    };

    // Parameters
    public final static String PARAMETER_WEBSITE = "uri";
    public final static String PARAMETER_PAGE = "c";

    public final static ResourceReference CSS = new CssResourceReference(Website.class, "Website.css");

    @Inject
    public IPageManager pageManager;

    public Website(PageParameters pageParameters) {
        super(new WebsiteModel(pageParameters));

        add(new DefaultTheme());

        final WebsiteStatus status = getStatus();
        final WebsiteConfig config = getConfig();

        String parentUri = ResourceUtils.getParentURI(config.getURI());
        String cssUri = ResourceUtils.getAbsoluteURI(parentUri, config.getCss());
        add(new CustomCssBehavior(cssUri));

        // Init currentPage
        if (status.getCurrentPage() == null) {
            status.setCurrentPage(config.getPages().get(0).getId());
        }

        add(new Label("windowTitle", config.getTitle()));

        add(new EmptyPanel("progressbar"));
        //add(new ProgressBar("progressbar", false));

        WebMarkupContainer header = new WebMarkupContainer("header");
        header.add(new ListView<PageConfig>("menu", new PropertyModel<List<PageConfig>>(this, "config.pages")) {

            @Override
            protected void populateItem(ListItem<PageConfig> item) {

                PageConfig pageConfig = item.getModelObject();

                PageParameters parameters = new PageParameters();
                parameters.add(PARAMETER_PAGE, pageConfig.getId());

                // If the website URI is not defined at Application level then add it as a parameter.
                if (Application.get().getMetaData(Website.WEBSITE_CONFIG) == null) {
                    parameters.add(PARAMETER_WEBSITE, config.getURI());
                }

                Link<String> link = new BookmarkablePageLink<String>("link", Website.class, parameters);
                link.add(new Label("name", pageConfig.getLabel()));

                String currentPage = status.getCurrentPage();

                item.add(link);

                if (currentPage.equals(pageConfig.getId())) {
                    link.getParent().add(new AttributeModifier("class", "active"));
                }

            }
        });
        add(header);

        String currentPage = status.getCurrentPage();

        add(pageManager.create("page", new PageModel(currentPage, (IModel<WebsiteStatus>) getDefaultModel())));

        if (config != null && config.getAuthorization() != null) {

            String role = config.getAuthorization();

            if (!AuthenticatedWebSession.get().getRoles().hasRole(role)) {
                OnexusWebApplication.get().restartResponseAtSignInPage();
            }

        }

        String bottom = config.getBottom();
        Label bottomLabel = new Label("bottom", bottom);
        bottomLabel.setVisible(bottom != null);
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

}
