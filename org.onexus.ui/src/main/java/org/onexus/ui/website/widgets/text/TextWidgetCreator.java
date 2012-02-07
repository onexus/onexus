package org.onexus.ui.website.widgets.text;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class TextWidgetCreator extends AbstractWidgetCreator<TextWidgetConfig, TextWidgetStatus> {
    
    public TextWidgetCreator() {
	super(TextWidgetConfig.class, "search-widget", "Search widget");
    }

    @Override
    protected Widget<?,?> build(String componentId, TextWidgetConfig config, IModel<TextWidgetStatus> statusModel) {
	return new TextWidget(componentId, config, statusModel);
    }

}
