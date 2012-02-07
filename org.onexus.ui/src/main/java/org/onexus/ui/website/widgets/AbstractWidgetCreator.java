package org.onexus.ui.website.widgets;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.AbstractWebsiteCreator;

public abstract class AbstractWidgetCreator<C extends WidgetConfig, S extends WidgetStatus> extends AbstractWebsiteCreator<WidgetConfig, WidgetStatus> implements IWidgetCreator {
    
    public AbstractWidgetCreator(Class<C> configType, String title, String description) {
	super(configType, title, description);
    }
   
    @SuppressWarnings("unchecked")
    @Override
    public Widget<?, ?> create(String componentId, WidgetConfig config, IModel<WidgetStatus> statusModel) {
	return build(componentId, (C) config, (IModel<S>) statusModel);
    }
    
    protected abstract Widget<?, ?> build(String componentId, C config, IModel<S> statusModel);

}
