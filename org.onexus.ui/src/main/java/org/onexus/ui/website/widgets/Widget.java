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
package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.onexus.core.query.Query;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;

public abstract class Widget<C extends WidgetConfig, S extends WidgetStatus> extends EventPanel {
    
    private IModel<S> statusModel;

    public Widget(String componentId, IModel<S> statusModel) {
        super(componentId);
        
        this.statusModel = statusModel;
    }

    public S getStatus() {
        return statusModel.getObject();
    }

    public C getConfig() {
        return (C) getStatus().getConfig();
    }

    protected Query getQuery() {
        PageStatus pageStatus = findParentStatus(statusModel, PageStatus.class);
        return (pageStatus == null ? null : pageStatus.buildQuery(getBaseUri()));
    }

    protected String getBaseUri() {
        WebsiteStatus websiteStatus = findParentStatus(statusModel, WebsiteStatus.class);
        return (websiteStatus == null ? null : ResourceUtils.getParentURI(websiteStatus.getConfig().getURI()));
    }

    protected String getReleaseUri() {
        BrowserPageStatus pageStatus = findParentStatus(statusModel, BrowserPageStatus.class);
        return (pageStatus==null ? getBaseUri() : ResourceUtils.concatURIs(getBaseUri(), pageStatus.getBase()));
    }

    public static <T> T findParentStatus(IModel<?> model, Class<T> statusClass) {

        Object obj = model.getObject();

        if (obj != null && statusClass.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (model instanceof IWrapModel) {
            IModel<?> parentModel = ((IWrapModel)model).getWrappedModel();
            return findParentStatus(parentModel, statusClass);
        }

        return null;
    }


}
