package org.onexus.ui.workspace.viewers;

import java.util.List;

import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public interface IViewersManager {
    
    public List<IViewerCreator> getViewerCreators(Resource resource);

}
