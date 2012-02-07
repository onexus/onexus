package org.onexus.ui.website.viewers;

import org.apache.wicket.model.IModel;

public interface ICollectionViewerManager {

    Viewer<?,?> create(String componentId, ViewerConfig config, IModel<ViewerStatus> statusModel);

}
