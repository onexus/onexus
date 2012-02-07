package org.onexus.ui.website.widgets.viewselector;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class ViewerSelectorWidgetCreator extends AbstractWidgetCreator<ViewerSelectorWidgetConfig, ViewerSelectorWidgetStatus> {
    
    public ViewerSelectorWidgetCreator() {
	super(ViewerSelectorWidgetConfig.class, "viewer-selector-widget", "Select another viewer");
    }

    @Override
    protected Widget<?,?> build(String componentId, ViewerSelectorWidgetConfig config, IModel<ViewerSelectorWidgetStatus> statusModel) {
	return new ViewerSelectorWidget(componentId, config, statusModel);
    }

}
