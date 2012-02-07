package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class FiltersWidgetCreator extends AbstractWidgetCreator<FiltersWidgetConfig, FiltersWidgetStatus> {
    
    public FiltersWidgetCreator() {
	super(FiltersWidgetConfig.class, "filters-widget", "Add predefined filters");
    }

    @Override
    protected Widget<?,?> build(String componentId, FiltersWidgetConfig config, IModel<FiltersWidgetStatus> statusModel) {
	return new FiltersWidget(componentId, config, statusModel);
    }

}
