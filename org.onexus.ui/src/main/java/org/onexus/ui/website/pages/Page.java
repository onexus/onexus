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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.events.EventPanel;
import org.onexus.ui.website.widgets.IWidgetManager;

import javax.inject.Inject;

public abstract class Page<C extends PageConfig, S extends PageStatus> extends EventPanel {

    @Inject
    private IWidgetManager widgetManager;

    private IPageModel<S> pageModel;

    public Page(String componentId, IPageModel<S> pageModel) {
        super(componentId);
        this.pageModel = pageModel;
    }

    public IWidgetManager getWidgetManager() {
        return widgetManager;
    }

    public void setWidgetManager(IWidgetManager widgetManager) {
        this.widgetManager = widgetManager;
    }

    public C getConfig() {
        return (C) pageModel.getConfig();
    }

    public S getStatus() {
        return (S) pageModel.getObject();
    }

    public IPageModel<S> getPageModel() {
        return pageModel;
    }

}
