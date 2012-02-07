/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.pages.browser;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.tabs.TabConfig;
import org.onexus.ui.website.tabs.TabStatus;

public class TabModel extends AbstractWrapModel<TabStatus> {

    private TabConfig tabConfig;
    private IModel<? extends BrowserPageStatus> browserModel;

    public TabModel(TabConfig tabConfig, IModel<? extends BrowserPageStatus> browserModel) {
        super();
        this.tabConfig = tabConfig;
        this.browserModel = browserModel;
    }


    @Override
    public TabStatus getObject() {

        BrowserPageStatus browserStatus = browserModel.getObject();

        if (browserStatus == null) {
            return tabConfig.getDefaultStatus();
        }

        TabStatus status = browserStatus.getTabStatus(tabConfig.getId());

        if (status == null) {
            status = tabConfig.getDefaultStatus();
            browserStatus.setTabStatus(status);
        }

        return status;
    }

    @Override
    public void setObject(TabStatus object) {

        if (browserModel != null) {
            browserModel.getObject().setTabStatus(object);
        }

    }


    @Override
    public IModel<?> getWrappedModel() {
        return browserModel;
    }

}
