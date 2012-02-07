package org.onexus.ui.website.tabs;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.AbstractWebsiteCreator;

public abstract class AbstractTabCreator<C extends TabConfig, S extends TabStatus> extends AbstractWebsiteCreator<TabConfig, TabStatus> implements ITabCreator {
    
    public AbstractTabCreator(Class<C> configType, String title, String description) {
	super(configType, title, description);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tab<?, ?> create(String componentId, TabConfig config, IModel<TabStatus> statusModel) {
	return build(componentId, (C) config, (IModel<S>) statusModel);
    }
    
    protected abstract Tab<?, ?> build(String componentId, C config, IModel<S> statusModel);

}
