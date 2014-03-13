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
package org.onexus.website.api.widgets;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.MarkupLoader;
import org.onexus.website.api.WebsiteStatus;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.utils.CustomCssBehavior;

public abstract class Widget<C extends WidgetConfig, S extends WidgetStatus> extends EventPanel implements IMarkupResourceStreamProvider {

    private IModel<S> statusModel;

    private transient MarkupLoader markupLoader;

    public Widget(String componentId, IModel<S> statusModel) {
        super(componentId);
        this.statusModel = statusModel;

        WidgetConfig config = getConfig();
        ORI parentOri = config.getPageConfig().getWebsiteConfig().getORI().getParent();
        add(new CustomCssBehavior(parentOri, config.getCss()));
    }

    public S getStatus() {
        return statusModel.getObject();
    }

    public C getConfig() {
        return (C) getStatus().getConfig();
    }

    protected Query getQuery() {
        PageStatus pageStatus = findParentStatus(statusModel, PageStatus.class);
        return pageStatus == null ? null : pageStatus.buildQuery(getWebsiteOri());
    }

    protected ORI getWebsiteOri() {
        WebsiteStatus websiteStatus = findParentStatus(WebsiteStatus.class);
        return websiteStatus == null ? null : websiteStatus.getConfig().getORI().getParent();
    }

    protected ORI getPageBaseOri() {
        BrowserPageStatus pageStatus = findParentStatus(BrowserPageStatus.class);
        return pageStatus == null ? getWebsiteOri() : new ORI(getWebsiteOri(), pageStatus.getBase());
    }

    protected <T> T findParentStatus(Class<T> statusClass) {
        return findParentStatus(statusModel, statusClass);
    }

    private static <T> T findParentStatus(IModel<?> model, Class<T> statusClass) {

        Object obj = model.getObject();

        if (obj != null && statusClass.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (model instanceof IWrapModel) {
            IModel<?> parentModel = ((IWrapModel) model).getWrappedModel();
            return findParentStatus(parentModel, statusClass);
        }

        return null;
    }

    public void onClose(AjaxRequestTarget target) {

    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {

        if (markupLoader == null) {
            markupLoader = new MarkupLoader(getConfig().getPageConfig().getWebsiteConfig().getORI(), getConfig().getMarkup());
        }

        return markupLoader.getMarkupResourceStream(container, containerClass);
    }

}
