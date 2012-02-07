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

import org.apache.wicket.model.IModel;

public class WebsiteModel implements IModel<WebsiteStatus>, IWebsiteModel {
    
    private WebsiteConfig websiteConfig;
    private WebsiteStatus websiteStatus;
    
    public WebsiteModel(WebsiteConfig websiteConfig, WebsiteStatus websiteStatus) {
	super();
	this.websiteConfig = websiteConfig;
	this.websiteStatus = websiteStatus;
    }

    @Override
    public WebsiteStatus getObject() {
	return websiteStatus;
    }

    @Override
    public IWebsiteConfig getConfig() {
	return websiteConfig;
    }
    
    @Override
    public void setObject(WebsiteStatus object) {
	this.websiteStatus = object;	
    }

    @Override
    public void detach() {
    }

    

}
