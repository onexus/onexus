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
