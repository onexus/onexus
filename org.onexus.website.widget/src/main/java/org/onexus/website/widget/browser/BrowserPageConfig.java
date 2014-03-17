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
package org.onexus.website.widget.browser;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceRegister;
import org.onexus.website.api.widget.WidgetConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ResourceAlias("browser")
@ResourceRegister({ TabConfig.class })
public class BrowserPageConfig extends WidgetConfig {

    @Valid
    private BrowserPageStatus defaultStatus;

    @NotNull @Valid
    private List<TabConfig> tabs = new ArrayList<TabConfig>();

    @NotNull @Valid
    private List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();

    public BrowserPageConfig() {
        super();
    }

    public TabConfig getTab(String tabId) {
        for (TabConfig tab : getTabs()) {
            if (tabId.equals(tab.getId())) {
                return tab;
            }
        }
        return null;
    }

    public List<TabConfig> getTabs() {
        return tabs;
    }

    public void setTabs(List<TabConfig> tabs) {
        this.tabs = tabs;
    }

    public List<WidgetConfig> getChildren() {
        return widgets;
    }

    public void setWidgets(List<WidgetConfig> widgets) {
        this.widgets = widgets;
    }

    public BrowserPageStatus createEmptyStatus() {
        return new BrowserPageStatus(getId());
    }

    public BrowserPageStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(BrowserPageStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }


}
