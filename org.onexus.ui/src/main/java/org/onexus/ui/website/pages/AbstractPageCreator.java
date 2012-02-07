package org.onexus.ui.website.pages;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.AbstractWebsiteCreator;

public abstract class AbstractPageCreator<C extends PageConfig, S extends PageStatus> extends AbstractWebsiteCreator<PageConfig, PageStatus> implements IPageCreator {
    
    public AbstractPageCreator(Class<C> configType, String title, String description) {
	super(configType, title, description);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Page<?, ?> create(String componentId, PageConfig config, IModel<PageStatus> statusModel) {
	return build(componentId, (C) config, (IModel<S>) statusModel);
    }
    
    protected abstract Page<?, ?> build(String componentId, C config, IModel<S> statusModel);

}
