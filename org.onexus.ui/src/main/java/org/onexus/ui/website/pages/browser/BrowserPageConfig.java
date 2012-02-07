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

import java.util.ArrayList;
import java.util.List;

import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.tabs.TabConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("page-browser")
public class BrowserPageConfig extends PageConfig {
    
    private BrowserPageStatus defaultStatus;

    private String defaultRelease;

    private List<TabConfig> tabs = new ArrayList<TabConfig>();

    public BrowserPageConfig() {
	super();
    }

    public List<TabConfig> getTabs() {
	return tabs;
    }
    
    public TabConfig getTab(String tabId) {
	
	if (tabs != null) {
	    for (TabConfig tab : tabs) {
		if (tab.getId().equals(tabId)) {
		    return tab;
		}
	    }
	}
	
	return null;
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
	return new BrowserPageStatus( getId() );
    }

    public BrowserPageStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(BrowserPageStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }
    
}
