package org.onexus.ui.website.viewers;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.AbstractWebsiteCreator;

public abstract class AbstractCollectionViewerCreator<C extends ViewerConfig, S extends ViewerStatus> extends
	AbstractWebsiteCreator<ViewerConfig, ViewerStatus> implements ICollectionViewerCreator {

    public AbstractCollectionViewerCreator(Class<C> configType, String title, String description) {
	super(configType, title, description);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Viewer<?, ?> create(String componentId, ViewerConfig config, IModel<ViewerStatus> statusModel) {
	return build(componentId, (C) config, (IModel<S>) statusModel);
    }

    protected abstract Viewer<?, ?> build(String componentId, C config, IModel<S> statusModel);

}
