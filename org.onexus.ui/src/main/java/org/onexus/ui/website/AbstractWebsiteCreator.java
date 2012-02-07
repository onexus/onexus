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

import org.onexus.ui.IResourceRegister;

public abstract class AbstractWebsiteCreator<C extends IWebsiteConfig, S extends IWebsiteStatus> implements IWebsiteCreator<C, S> {
    
    private Class<? extends C> configType;
    private String title;
    private String description;
    
    public AbstractWebsiteCreator(Class<? extends C> configType, String title, String description) {
	super();
	this.configType = configType;
	this.title = title;
	this.description = description;
    }
    
    public boolean canCreate(C config) {
	if (config == null) {
	    return false;
	}
	
	return config.getClass().equals(configType);
    }
    
    public String getTitle() {
	return title;
    }

    public String getDescription() {
	return description;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void register(IResourceRegister resourceRegister) {
	resourceRegister.register(configType);	
    }

}
