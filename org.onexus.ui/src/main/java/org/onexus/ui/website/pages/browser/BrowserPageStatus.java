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

import org.onexus.core.query.FixedEntity;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.tabs.TabStatus;

import java.util.HashSet;
import java.util.Set;

public class BrowserPageStatus extends PageStatus {

    private String releaseURI;

    private Set<FixedEntity> fixedEntities;

    private String currentTabId;

    private Set<TabStatus> tabStatus;

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    public BrowserPageStatus(TabStatus defaultStatus) {
        setTabStatus(defaultStatus);
        setCurrentTabId(defaultStatus.getId());
    }

    public String getReleaseURI() {
        return releaseURI;
    }

    public void setReleaseURI(String releaseURI) {
        this.releaseURI = releaseURI;
    }

    public Set<FixedEntity> getFixedEntities() {
        if (fixedEntities == null) {
            fixedEntities = new HashSet<FixedEntity>();
        }
        return fixedEntities;
    }

    public void setFixedEntities(Set<FixedEntity> fixedEntities) {
        this.fixedEntities = fixedEntities;
    }

    public String getCurrentTabId() {
        return currentTabId;
    }

    public void setCurrentTabId(String currentTabId) {
        this.currentTabId = currentTabId;
    }

    public TabStatus getCurrentTabStatus() {
        return getTabStatus(getCurrentTabId());
    }

    public Set<TabStatus> getTabStatus() {
        if (tabStatus == null) {
            tabStatus = new HashSet<TabStatus>();
        }
        return tabStatus;
    }

    public TabStatus getTabStatus(String tabId) {
        for (TabStatus status : getTabStatus()) {
            if (status.getId().equals(tabId)) {
                return status;
            }
        }
        return null;
    }

    public void setTabStatus(TabStatus tabStatus) {
        if (tabStatus != null) {
            this.getTabStatus().add(tabStatus);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getTabStatus() == null) ? 0 : getTabStatus().hashCode());
        result = prime * result
                + ((getFixedEntities() == null) ? 0 : getFixedEntities().hashCode());
        result = prime * result
                + ((releaseURI == null) ? 0 : releaseURI.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrowserPageStatus other = (BrowserPageStatus) obj;
        if (getTabStatus() == null) {
            if (other.getTabStatus() != null)
                return false;
        } else if (!getTabStatus().equals(other.getTabStatus()))
            return false;
        if (getFixedEntities() == null) {
            if (other.getFixedEntities() != null)
                return false;
        } else if (!getFixedEntities().equals(other.getFixedEntities()))
            return false;
        if (releaseURI == null) {
            if (other.releaseURI != null)
                return false;
        } else if (!releaseURI.equals(other.releaseURI))
            return false;
        return true;
    }

}
