package org.onexus.ui.website.tabs;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.IWebsiteCreator;

public interface ITabCreator extends IWebsiteCreator<TabConfig, TabStatus> {
    
    public boolean canCreate(TabConfig config);
    
    public String getTitle();
    
    public String getDescription();
    
    public Tab<?,?> create(String componentId, TabConfig config, IModel<TabStatus> statusModel);

}
