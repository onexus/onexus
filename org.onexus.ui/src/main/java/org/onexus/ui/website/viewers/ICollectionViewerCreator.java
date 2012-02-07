package org.onexus.ui.website.viewers;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.IWebsiteCreator;

public interface ICollectionViewerCreator extends IWebsiteCreator<ViewerConfig, ViewerStatus> {
    
    public Viewer<?,?> create(String componentId, ViewerConfig config, IModel<ViewerStatus> statusModel);

}
