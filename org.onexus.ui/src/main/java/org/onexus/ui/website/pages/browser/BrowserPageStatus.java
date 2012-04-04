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
package org.onexus.ui.website.pages.browser;

import org.onexus.core.query.FixedEntity;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrowserPageStatus extends PageStatus {
    
    private String releaseURI;
    
    private String currentTabId;
    
    private String currentView;

    private Set<FixedEntity> fixedEntities = new HashSet<FixedEntity>();

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    public String getReleaseURI() {
        return releaseURI;
    }

    public String getCurrentTabId() {
        return currentTabId;
    }

    public void setCurrentTabId(String currentTabId) {
        if (currentTabId != null && currentTabId.equals(this.currentView = null)) {
            this.currentView = null;
        }
        this.currentTabId = currentTabId;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    public void setReleaseURI(String releaseURI) {
        this.releaseURI = releaseURI;
    }

    public Set<FixedEntity> getFixedEntities() {
        return fixedEntities;
    }

    public void setFixedEntities(Set<FixedEntity> fixedEntities) {
        this.fixedEntities = fixedEntities;
    }
}
