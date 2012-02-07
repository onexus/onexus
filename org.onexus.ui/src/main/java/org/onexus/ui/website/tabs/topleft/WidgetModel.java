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
