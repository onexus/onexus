package org.onexus.ui.editor;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IViewerCreator;

public class EditorViewerTab implements IViewerCreator {
    
    public EditorViewerTab() {
	super();
    }

    @Override
    public String getTitle() {
	return "Editor";
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
	return new EditorViewer(containerId, model);
    }

    @Override
    public boolean isVisible(Class<? extends Resource> resourceType) {
	return true;
    }

    @Override
    public double getOrder() {
	return 0;
    }

    

}
