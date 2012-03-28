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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.utils.reflection.ListComposer;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("browser")
public class BrowserPageConfig extends PageConfig {

    private BrowserPageStatus defaultStatus;

    private String defaultRelease;

    private List<TabConfig> tabs = new ArrayList<TabConfig>();

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

    public String getDefaultRelease() {
        return defaultRelease;
    }

    public void setDefaultRelease(String defaultRelease) {
        this.defaultRelease = defaultRelease;
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

    private transient List<WidgetConfig> widgetConfigList;

    @Override
    public List<WidgetConfig> getWidgetConfigs() {

        if (widgetConfigList == null) {
            this.widgetConfigList = new ListComposer<WidgetConfig>(this, "tabs.views.widgets");
        }

        return this.widgetConfigList;
    }


}
