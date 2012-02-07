package org.onexus.ui.website.pages.browser;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.tabs.TabConfig;
import org.onexus.ui.website.tabs.TabStatus;

public class TabModel extends AbstractWrapModel<TabStatus> {
    
    private TabConfig  tabConfig;
    private IModel<? extends BrowserPageStatus> browserModel;
    
    public TabModel(TabConfig tabConfig, IModel<? extends BrowserPageStatus> browserModel) {
	super();
	this.tabConfig = tabConfig;
	this.browserModel = browserModel;
    }


    @Override
    public TabStatus getObject() {
	
	BrowserPageStatus browserStatus = browserModel.getObject();
	
	if (browserStatus == null) {
	    return tabConfig.getDefaultStatus();
	}
	
	TabStatus status = browserStatus.getTabStatus(tabConfig.getId());
	
	if (status == null) {
	    status = tabConfig.getDefaultStatus();
	    browserStatus.setTabStatus(status);
	}
		
	return status;
    }
    
    @Override
    public void setObject(TabStatus object) {
	
	if (browserModel != null) {
	    browserModel.getObject().setTabStatus(object);
	}
	
    }


    @Override
    public IModel<?> getWrappedModel() {
	return browserModel;
    }

}
