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

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.core.query.Query;
import org.onexus.ui.website.Website;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.widgets.IWidgetManager;
import org.onexus.ui.website.widgets.WidgetStatus;

import javax.inject.Inject;
import java.util.List;

public abstract class Page<C extends PageConfig, S extends PageStatus> extends EventPanel {

    @Inject
    private IWidgetManager widgetManager;

    public Page(String componentId, IModel<S> pageModel) {
        super(componentId, pageModel);
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

}
