package org.onexus.ui.workspace.tree;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Workspace;
import org.onexus.ui.OnexusWebSession;

public class WorkspaceTreeModel extends AbstractReadOnlyModel<TreeModel> {

    private IModel<Resource> currentResource;
    
    private transient TreeModel tree = null;

    public WorkspaceTreeModel(IModel<Resource> currentResource) {
	super();
	this.currentResource = currentResource;
    }

    @Override
    public TreeModel getObject() {
	
	if (tree != null) {
	    return tree;
	}
	
	Workspace workspace = getCurrentWorkspace();
	
	if (workspace == null) {
	    return new DefaultTreeModel(ResourceNode.create(null));
	}
	
	MutableTreeNode workspaceNode = createTreeNode(workspace);
	tree = new DefaultTreeModel(workspaceNode);

	return tree;
    }

    /**
     * @return The workspace of the current resource selection
     */
    private Workspace getCurrentWorkspace() {

	Resource resource = currentResource.getObject();

	if (resource != null) {

	    String resourceURI = resource.getURI();
	    
	    List<Workspace> workspaces = getResourceManager().loadChildren(Workspace.class, null);
	    
	    for (Workspace workspace : workspaces) {
		if (resourceURI.startsWith(workspace.getURI())) {
		    return workspace;
		}
	    }
	    
	}

	return null;
    }

    /**
     * @param parentResource 
     * @return A treeNode with all the children resources nodes added.
     */
    private MutableTreeNode createTreeNode(Resource parentResource) {

	if (parentResource == null) {
	    return null;
	}
	
	DefaultMutableTreeNode parentNode = ResourceNode.create(parentResource);

	List<Resource> resources = getResourceManager().loadChildren(Resource.class, parentResource.getURI());

	if (!resources.isEmpty()) {
	    for (Resource resource : resources) {
		parentNode.add(createTreeNode(resource));
	    }
	}

	return parentNode;
    }

    protected IResourceManager getResourceManager() {
	return OnexusWebSession.get().getResourceManager();
    }
   
}
