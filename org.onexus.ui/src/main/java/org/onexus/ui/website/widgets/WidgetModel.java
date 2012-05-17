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

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.PageStatus;

import javax.net.ssl.SSLEngineResult;

public class WidgetModel<S extends WidgetStatus> extends AbstractWrapModel<S> {

    private String widgetId;
    private IModel<? extends PageStatus> pageModel;

    public WidgetModel(String widgetId, IModel<? extends PageStatus> pageModel) {
        super();
        this.widgetId = widgetId;
        this.pageModel = pageModel;
    }

    @Override
    public S getObject() {

        S status = (S) getPageStatus().getWidgetStatus(widgetId);

        if (status == null) {
            status = (S) getConfig().newStatus();
            setObject(status);
        }

        // Check config is set
        if (status.getConfig() == null) {
            status.setConfig(getConfig());
        }

        return status;
    }

    private PageStatus getPageStatus() {
        return pageModel.getObject();
    }

    private WidgetConfig getConfig() {

        WidgetConfig config = getPageStatus().getConfig().getWidget(widgetId);

        // Check pageConfig is set
        if (config.getPageConfig() == null) {
            config.setPageConfig(getPageStatus().getConfig());
        }

        return config;
    }

    @Override
    public void setObject(S object) {
        pageModel.getObject().setWidgetStatus(object);
    }

    @Override
    public IModel<?> getWrappedModel() {
        return pageModel;
    }
}
