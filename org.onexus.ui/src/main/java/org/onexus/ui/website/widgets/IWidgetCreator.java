package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.IWebsiteCreator;

public interface IWidgetCreator extends IWebsiteCreator<WidgetConfig, WidgetStatus> {
    
    public Widget<?,?> create(String componentId, WidgetConfig config, IModel<WidgetStatus> statusModel);

}
