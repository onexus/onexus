package org.onexus.ui.website.widgets.search;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class SearchWidgetCreator extends AbstractWidgetCreator<SearchWidgetConfig, SearchWidgetStatus> {
    
    public SearchWidgetCreator() {
	super(SearchWidgetConfig.class, "search-widget", "Search widget");
    }

    @Override
    protected Widget<?,?> build(String componentId, SearchWidgetConfig config, IModel<SearchWidgetStatus> statusModel) {
	return new SearchWidget(componentId, config, statusModel);
    }

}
