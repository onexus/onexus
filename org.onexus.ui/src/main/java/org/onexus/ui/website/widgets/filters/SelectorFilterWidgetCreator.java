package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class SelectorFilterWidgetCreator extends AbstractWidgetCreator<SelectorFilterWidgetConfig, SelectorFilterWidgetStatus> {
    
    public SelectorFilterWidgetCreator() {
	super(SelectorFilterWidgetConfig.class, "selector-filters-widget", "Add predefined filters as dropdown");
    }

    @Override
    protected Widget<?,?> build(String componentId, SelectorFilterWidgetConfig config, IModel<SelectorFilterWidgetStatus> statusModel) {
	return new SelectorFilterWidget(componentId, config, statusModel);
    }

}
