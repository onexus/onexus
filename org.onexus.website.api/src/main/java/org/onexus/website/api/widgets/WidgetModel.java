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

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;

public class WidgetModel<S extends WidgetStatus> extends AbstractWrapModel<S> {

    private String widgetId;
    private IModel<? extends WidgetStatus> parentModel;

    public WidgetModel(String widgetId, IModel<? extends WidgetStatus> parentModel) {
        super();
        this.widgetId = widgetId;
        this.parentModel = parentModel;
    }

    @Override
    public S getObject() {

        S status = (S) getParentStatus().getChild(widgetId);

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

    private WidgetStatus getParentStatus() {
        return parentModel.getObject();
    }

    private WidgetConfig getConfig() {

        WidgetConfig parentConfig = getParentStatus().getConfig();

        WidgetConfig config = parentConfig.getChild(widgetId);

        // Check pageConfig is set
        if (config.getParentConfig() == null) {
            config.setParentConfig(parentConfig);
            config.setWebsiteConfig(parentConfig.getWebsiteConfig());
        }

        return config;
    }

    @Override
    public void setObject(S object) {
        parentModel.getObject().setWidgetStatus(object);
    }

    @Override
    public IModel<?> getWrappedModel() {
        return parentModel;
    }
}
