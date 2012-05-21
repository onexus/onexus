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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.onexus.core.ISourceManager;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.IPageManager;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageModel;
import org.onexus.ui.workspace.progressbar.ProgressBar;

import javax.inject.Inject;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Website extends WebPage {

    public final static MetaDataKey<WebsiteConfig> WEBSITE_CONFIG = new MetaDataKey<WebsiteConfig>() {};

    // Parameters
    public final static String PARAMETER_WEBSITE = "onexus-website";
    public final static String PARAMETER_STATUS = "onexus-status";
    public final static String PARAMETER_PAGE = "onexus-page";

    public final static ResourceReference CSS = new CssResourceReference(Website.class, "Website.css");

    private transient ResourceReference WEBSITE_CSS;

    @Inject
    public IPageManager pageManager;

    @Inject
    public ISourceManager sourceManager;

    public Website(PageParameters pageParameters) {
        super(new WebsiteModel(pageParameters));

        final WebsiteStatus status = getStatus();
        final WebsiteConfig config = getConfig();

        // Init currentPage
        if (status.getCurrentPage() == null) {
            status.setCurrentPage(config.getPages().get(0).getId());
        }

        add(new ProgressBar("progressbar", false));
        add(new Label("windowTitle", config.getTitle()));

        boolean showHeader = (config.getShowHeader() == null) ? true : config.getShowHeader();
        WebMarkupContainer header = new WebMarkupContainer("header");
        header.add( new AttributeModifier("class", (showHeader?"max":"min")) );
        WebMarkupContainer top = new WebMarkupContainer("top");
        top.add(new Label("title", config.getTitle()));
        top.setVisible(showHeader);
        header.add(top);
        add(header);

        header.add(new ListView<PageConfig>("menu", new PropertyModel<List<PageConfig>>(this, "config.pages")) {

            @Override
            protected void populateItem(ListItem<PageConfig> item) {

                PageConfig pageConfig = item.getModelObject();

                PageParameters parameters = new PageParameters();
                parameters.add(PARAMETER_PAGE, pageConfig.getId());
                parameters.add(PARAMETER_WEBSITE, config.getURI());
                Link<String> link = new BookmarkablePageLink<String>("link", Website.class, parameters);
                link.add(new Label("name", pageConfig.getLabel()));

                String currentPage = status.getCurrentPage();

                item.add(link);

                if (currentPage.equals(pageConfig.getId())) {
                    link.getParent().add(new AttributeModifier("class", "current"));
                }

            }
        });

        String currentPage = status.getCurrentPage();

        add(pageManager.create("page", new PageModel(currentPage, (IModel<WebsiteStatus>) getDefaultModel() )));

        if (config != null && config.getAuthorization() != null) {

            String role = config.getAuthorization();

            if (!AuthenticatedWebSession.get().getRoles().hasRole(role)) {
                OnexusWebApplication.get().restartResponseAtSignInPage();
            }

        }

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (WEBSITE_CSS == null) {

            WebsiteConfig config = getConfig();
            String css = config.getCss();

            if (css != null) {
                String parentUri = ResourceUtils.getParentURI(config.getURI());
                String fileUri = ResourceUtils.getAbsoluteURI(parentUri, css);

                List<URL> urls = sourceManager.retrieve(fileUri);

                if (urls.isEmpty()) {
                    WEBSITE_CSS = CSS;
                } else {

                    ResourceReference fileResource = null;
                    try {
                        URI uri = urls.get(0).toURI();
                        IResource resource = new ResourceStreamResource(new FileResourceStream(new File(uri)));
                        String resourceName = "css-" + Integer.toHexString(uri.toString().hashCode());
                        Application.get().getSharedResources().add(Website.class, resourceName, null, null, null, resource);
                        fileResource = Application.get().getSharedResources().get(Website.class, resourceName, null, null, null, true);
                    } catch (URISyntaxException e) {

                    }

                    if (fileResource != null) {
                       WEBSITE_CSS = fileResource;
                    } else {
                       WEBSITE_CSS = CSS;
                    }
                }

            } else {
               WEBSITE_CSS = CSS;
            }
        }


        response.render(CssHeaderItem.forReference(WEBSITE_CSS));
    }

    public WebsiteStatus getStatus() {
        return (WebsiteStatus) getDefaultModelObject();
    }

    public WebsiteConfig getConfig() {
        return getStatus().getConfig();
    }

}
