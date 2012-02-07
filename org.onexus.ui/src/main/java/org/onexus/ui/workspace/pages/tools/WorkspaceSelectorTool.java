package org.onexus.ui.workspace.pages.tools;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Workspace;
import org.onexus.ui.workspace.pages.ResourcesPage;

public class WorkspaceSelectorTool extends AbstractTool<Workspace> {
    
    @Inject
    private IResourceManager resourceManager;

    public WorkspaceSelectorTool() {
	super(new Model<Workspace>());
	
	add(new DropDownChoice<Workspace>("resources-toolbar-selector", getModel(), new WorkspacesListModel(), new WorkspaceRenderer()) {

	    @Override
	    protected void onSelectionChanged(Workspace newSelection) {
		
		if (newSelection != null) {
		    PageParameters parameters = new PageParameters();
		    parameters.add(ResourcesPage.RESOURCE, newSelection.getURI());
		    setResponsePage(ResourcesPage.class, parameters);
		} else {
		    setResponsePage(ResourcesPage.class);
		}		
		
	    }

	    @Override
	    protected boolean wantOnSelectionChangedNotifications() {
		return true;
	    }
	    
	});
    }
    
    private class WorkspacesListModel extends AbstractReadOnlyModel<List<Workspace>> {

	@Override
	public List<Workspace> getObject() {
	    
	    List<Workspace> workspaces = resourceManager.loadChildren(Workspace.class, null);		
	    return workspaces;
	    
	}
	
    }
    
    private class WorkspaceRenderer implements IChoiceRenderer<Workspace> {

	@Override
	public Object getDisplayValue(Workspace workspace) {
	    return workspace.getName();
	}

	@Override
	public String getIdValue(Workspace object, int index) {
	    return Integer.toString(index);
	}
	
    }
    
}
