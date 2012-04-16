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

import org.apache.wicket.*;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.website.pages.IPageManager;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageModel;
import org.onexus.ui.website.widgets.bookmark.StatusEncoder;
import org.onexus.ui.workspace.progressbar.ProgressBar;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Website extends WebPage {

    public final static MetaDataKey<WebsiteConfig> WEBSITE_CONFIG = new MetaDataKey<WebsiteConfig>() {
    };
    public final static MetaDataKey<WebsiteStatus> WEBSITE_STATUS = new MetaDataKey<WebsiteStatus>() {
    };

    public final static String PARAMETER_WEBSITE = "onexus-website";
    public final static String PARAMETER_STATUS = "onexus-status";
    public final static String PARAMETER_PAGE = "onexus-page";

    public final static CssResourceReference CSS = new CssResourceReference(Website.class, "Website.css");
    public final static JavaScriptResourceReference JS = new JavaScriptResourceReference(Website.class, "utils/ajax-loader.js");

    @Inject
    public IPageManager pageManager;

    private WebsiteConfig websiteConfig;
    private WebsiteStatus websiteStatus;

    public Website(PageParameters pageParameters) {

        initConfig(pageParameters);
        initStatus(pageParameters);

        add(new ProgressBar("progressbar"));
        add(new Label("windowTitle", websiteConfig.getTitle()));
        add(new Label("title", websiteConfig.getTitle()));

        add(new ListView<PageConfig>("menu", new PropertyModel<List<PageConfig>>(this, "websiteConfig.pages")) {

            @Override
            protected void populateItem(ListItem<PageConfig> item) {

                PageConfig pageConfig = item.getModelObject();

                PageParameters parameters = new PageParameters();
                parameters.add(PARAMETER_PAGE, pageConfig.getId());
                parameters.add(PARAMETER_WEBSITE, websiteConfig.getURI());
                Link<String> link = new BookmarkablePageLink<String>("link", Website.class, parameters);
                link.add(new Label("name", pageConfig.getLabel()));

                String currentPage = websiteStatus.getCurrentPage();

                item.add(link);

                if (currentPage.equals(pageConfig.getId())) {
                    link.getParent().add(new AttributeModifier("class", "current"));
                }

            }
        });

        String currentPage = websiteStatus.getCurrentPage();
        PageConfig pageConfig = websiteConfig.getPage(currentPage);

        add(pageManager.create("page", new PageModel(pageConfig, new WebsiteModel(websiteConfig, websiteStatus))));

        if (websiteConfig.getAuthorization() != null) {
            MetaDataRoleAuthorizationStrategy.authorize(this, Component.RENDER, websiteConfig.getAuthorization());
        }

    }

    private void initConfig(PageParameters pageParameters) {

        // Application level
        websiteConfig = Application.get().getMetaData(WEBSITE_CONFIG);

        // Session level
        if (websiteConfig == null) {
            websiteConfig = Session.get().getMetaData(WEBSITE_CONFIG);
        }

        // URL level
        if (websiteConfig == null) {
            StringValue websiteURI = pageParameters.get(PARAMETER_WEBSITE);
            if (!websiteURI.isNull()) {
                websiteConfig = OnexusWebSession.get().getResourceManager()
                        .load(WebsiteConfig.class, websiteURI.toString());
            }
        }

    }

    private void initStatus(PageParameters pageParameters) {

        // Session level
        websiteStatus = Session.get().getMetaData(WEBSITE_STATUS);

        // URL level
        if (websiteStatus == null) {
            StringValue statusENCODED = pageParameters.get(PARAMETER_STATUS);

            if (!statusENCODED.isNull()) {
                try {
                    StatusEncoder statusEncoder = new StatusEncoder(getClass().getClassLoader());
                    websiteStatus = statusEncoder.decodeStatus(statusENCODED.toString());
                } catch (UnsupportedEncodingException e) {
                    // TODO
                }
            }
        }

        // Default config status
        if (websiteStatus == null) {
            websiteStatus = websiteConfig.getDefault();
        }

        // Empty status
        if (websiteStatus == null) {
            websiteStatus = websiteConfig.createEmptyStatus();
        }

        // Set current page
        StringValue pageId = pageParameters.get(PARAMETER_PAGE);
        if (!pageId.isEmpty()) {
            websiteStatus.setCurrentPage(pageId.toString());
        } else {
            if (websiteConfig.getPages() != null && !websiteConfig.getPages().isEmpty()) {
                websiteStatus.setCurrentPage(websiteConfig.getPages().get(0).getId());
            } else {
                throw new WicketRuntimeException("No page definition in this website. Add at least one page.");
            }
        }

    }



    public WebsiteConfig getWebsiteConfig() {
        return websiteConfig;
    }

    public WebsiteStatus getWebsiteStatus() {
        return websiteStatus;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(CSS);
        response.renderJavaScriptReference(JS);
    }

}
