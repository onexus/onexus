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
package org.onexus.ui.website.pages;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.string.*;
import org.onexus.core.IDataManager;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.widgets.IWidgetManager;

import javax.inject.Inject;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public abstract class Page<C extends PageConfig, S extends PageStatus> extends EventPanel {


    private ResourceReference PAGE_CSS;

    @Inject
    private IWidgetManager widgetManager;

    @Inject
    private IDataManager dataManager;

    public Page(String componentId, IModel<S> pageModel) {
        super(componentId, pageModel);

        if (PAGE_CSS == null) {
            PAGE_CSS = getCssResourceReference(getClass(), dataManager, getConfig());
        }

    }

    public IWidgetManager getWidgetManager() {
        return widgetManager;
    }

    public void setWidgetManager(IWidgetManager widgetManager) {
        this.widgetManager = widgetManager;
    }

    public IModel<S> getModel() {
        return (IModel<S>) getDefaultModel();
    }

    public S getStatus() {
        return (S) getDefaultModelObject();
    }

    public C getConfig() {
        S status = getStatus();
        return (status == null ? null : (C) status.getConfig());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(PAGE_CSS));
    }

    protected boolean isEmbed() {
        StringValue embed = getPage().getPageParameters().get("embed");
       return embed.toBoolean(false);
    }

    public static ResourceReference getCssResourceReference(Class<?> scope, IDataManager dataManager, PageConfig config) {

        if (config.getCss() != null) {
                WebsiteConfig websiteConfig = config.getWebsiteConfig();
                String parentUri = (websiteConfig != null) ? ResourceUtils.getParentURI(websiteConfig.getURI()) : null;
                String cssUri = ResourceUtils.getAbsoluteURI(parentUri, config.getCss());
                List<URL> urls = dataManager.retrieve(cssUri);

                try {

                    URI uri = urls.get(0).toURI();
                    IResource resource = new ResourceStreamResource(new FileResourceStream(new File(uri)));
                    String resourceName = "css-" + Integer.toHexString(uri.toString().hashCode());

                    Application.get().getSharedResources().add(scope, resourceName, null, null, null, resource);
                    return Application.get().getSharedResources().get(scope, resourceName, null, null, null, true);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
        }

        return new CssResourceReference(scope, scope.getSimpleName() + ".css");

    }
}
