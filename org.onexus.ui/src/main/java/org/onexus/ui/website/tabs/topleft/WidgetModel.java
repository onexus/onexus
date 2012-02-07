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
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetStatus;

public class WidgetModel extends AbstractWrapModel<WidgetStatus> {
    
    private WidgetConfig widgetConfig;
    private IModel<? extends TabStatus> tabModel;
    
    public WidgetModel(WidgetConfig widgetConfig, IModel<? extends TabStatus> tabModel) {
	super();
	this.widgetConfig = widgetConfig;
	this.tabModel = tabModel;
    }


    @Override
    public WidgetStatus getObject() {
	
	TabStatus tabStatus = tabModel.getObject();
	
	if (tabStatus == null) {
	    return widgetConfig.getDefaultStatus();
	}
	
	WidgetStatus status = tabStatus.getWidgetStatus(widgetConfig.getId());
	
	if (status == null) {
	    status = widgetConfig.getDefaultStatus();
	    tabStatus.setWidgetStatus(status);
	}
		
	return status;
    }


    @Override
    public IModel<?> getWrappedModel() {
	return tabModel;
    }

}
