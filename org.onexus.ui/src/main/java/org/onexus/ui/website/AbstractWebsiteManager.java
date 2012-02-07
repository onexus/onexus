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

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.osgi.framework.ServiceReference;

public class AbstractWebsiteManager<P extends Panel, C extends IWebsiteConfig, S extends IWebsiteStatus> {
    
    private IResourceRegister resourceRegister;
    
    private List<? extends IWebsiteCreator<C,S>> creators;

    @SuppressWarnings("unchecked")
    public P create(String componentId, C config, IModel<S> statusModel) {
		
	for (IWebsiteCreator<C,S> creator : creators) {
	    if (creator.canCreate(config)) {
		return (P) creator.create(componentId, config, statusModel);
	    }
	}
	
	return null;
    }

    public List<? extends IWebsiteCreator<C, S>> getCreators() {
        return creators;
    }

    public void setCreators(List<? extends IWebsiteCreator<C, S>> creators) {
        this.creators = creators;
    }

    public IResourceRegister getResourceRegister() {
        return resourceRegister;
    }

    public void setResourceRegister(IResourceRegister resourceRegister) {
        this.resourceRegister = resourceRegister;
    }
    
    public void bindCreators(ServiceReference serviceRef) {
	
	if (resourceRegister != null && creators != null) {
	    for (IWebsiteCreator<C,S> bc : creators) {
		bc.register(resourceRegister);
	    }
	}
    }
    
    public void unbindCreators(ServiceReference serviceRef) {
	// Nothing to do
    }

}
