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
package org.onexus.ui.website;

import java.util.HashSet;
import java.util.Set;

import org.onexus.ui.website.pages.PageStatus;

public class WebsiteStatus implements IWebsiteStatus {
    
    private String currentPageId;
    private Set<PageStatus> pageStatus;
    
    public PageStatus getPageStatus(String id) {
	for (PageStatus status : getPageStatus()) {
	    if (status.getId().equals(id)) {
		return status;
	    }
	}
	return null;
    }
    
    
    public String getCurrentPageId() {
        return currentPageId;
    }


    public void setCurrentPageId(String currentPageId) {
        this.currentPageId = currentPageId;
    }

    public Set<PageStatus> getPageStatus() {
	if (pageStatus==null) {
	    pageStatus = new HashSet<PageStatus>();
	}
	return pageStatus;
    }

    public void setPageStatus(PageStatus pageStatus) {
	
	if (pageStatus != null) {
	    getPageStatus().add(pageStatus);
	}
    }


    public PageStatus getCurrentPageStatus() {
	return getPageStatus(getCurrentPageId());
    }

    
}
