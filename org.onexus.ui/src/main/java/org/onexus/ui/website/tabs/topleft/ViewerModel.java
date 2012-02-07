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
package org.onexus.ui.website.tabs.topleft;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.tabs.TabStatus;
import org.onexus.ui.website.viewers.ViewerConfig;
import org.onexus.ui.website.viewers.ViewerStatus;

public class ViewerModel extends AbstractWrapModel<ViewerStatus> {
    
    private ViewerConfig viewerConfig;
    private IModel<? extends TabStatus> tabModel;

    public ViewerModel(ViewerConfig viewerConfig, IModel<? extends TabStatus> tabModel) {
	super();
	this.viewerConfig = viewerConfig;
	this.tabModel = tabModel;
    }
    
    @Override
    public ViewerStatus getObject() {
	
	TabStatus tabStatus = tabModel.getObject();
	
	if (tabStatus == null) {
	    return viewerConfig.getDefaultStatus(); 
	}
	
	ViewerStatus viewerStatus = tabStatus.getViewerStatus(viewerConfig.getId());
	
	if (viewerStatus == null) {
	    viewerStatus = viewerConfig.getDefaultStatus();
	    tabStatus.setViewerStatus(viewerStatus);
	}
	
	return viewerStatus;
    }

    @Override
    public IModel<?> getWrappedModel() {
	return tabModel;
    }
    
    

}
