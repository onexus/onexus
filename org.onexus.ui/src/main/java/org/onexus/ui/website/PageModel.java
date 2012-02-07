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

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageStatus;

public class PageModel extends AbstractWrapModel<PageStatus> implements IWebsiteModel {
    
    private PageConfig  pageConfig;
    private IModel<? extends WebsiteStatus> websiteModel;
    
    private transient PageStatus pageStatus;
    
    public PageModel(PageConfig pageConfig, IModel<? extends WebsiteStatus> websiteModel) {
	super();
	this.pageConfig = pageConfig;
	this.websiteModel = websiteModel;
    }


    @Override
    public PageStatus getObject() {
	
	if (pageStatus == null) {
	    
	    if (websiteModel != null && websiteModel.getObject() != null) {
		setObject( websiteModel.getObject().getPageStatus(pageConfig.getId()) );
	    } else {
		setObject( pageConfig.getDefaultStatus() );
	    }
	    
	    if (pageStatus == null) {
		setObject( pageConfig.createEmptyStatus() );
	    }
	}
		
	return pageStatus;
    }
    
    @Override
    public void setObject(PageStatus pageStatus) {
	
	this.pageStatus = pageStatus;
	
	if (websiteModel != null) {
	    WebsiteStatus websiteStatus = websiteModel.getObject();
	    
	    if (websiteStatus != null) {
		websiteStatus.setPageStatus(pageStatus);
	    }
	}
	
    }

    @Override
    public IModel<?> getWrappedModel() {
	return websiteModel;
    }


    @Override
    public IWebsiteConfig getConfig() {
	return pageConfig;
    }

}
