package org.onexus.ui.workspace.viewers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public class DefaultViewersManager implements IViewersManager {

    public static final ViewerCreatorComparator VIEWER_CREATOR_COMPRATOR = new ViewerCreatorComparator();

    private List<IViewerCreator> viewerCreators;

    public DefaultViewersManager() {
	super();
    }

    @Override
    public List<IViewerCreator> getViewerCreators(Resource resource) {

	if (resource == null) {
	    return Collections.emptyList();
	}

	List<IViewerCreator> result = new ArrayList<IViewerCreator>();
	CollectionUtils.select(viewerCreators, new VisiblePredicate(resource), result);
	Collections.sort(result, VIEWER_CREATOR_COMPRATOR);

	return result;

    }

    public List<IViewerCreator> getViewerCreators() {
        return viewerCreators;
    }

    public void setViewerCreators(List<IViewerCreator> viewerCreators) {
        this.viewerCreators = viewerCreators;
    }

    private static class VisiblePredicate implements Predicate, Serializable {

	private Resource resource;

	public VisiblePredicate(Resource resource) {
	    super();
	    this.resource = resource;
	}

	@Override
	public boolean evaluate(Object object) {

	    if (object instanceof IViewerCreator) {
		IViewerCreator creator = (IViewerCreator) object;

		if (resource == null) {
		    return false;
		}

		return creator.isVisible(resource.getClass());
	    }

	    return false;
	}

    }

    private static class ViewerCreatorComparator implements Comparator<IViewerCreator> {

	@Override
	public int compare(IViewerCreator o1, IViewerCreator o2) {
	    return Double.compare(o1.getOrder(), o2.getOrder());
	}

    }

}
