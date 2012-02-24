/**
 *  Copyright 2011 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.ui.workspace.tree;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Workspace;
import org.onexus.ui.OnexusWebSession;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkspaceTreeModel extends AbstractReadOnlyModel<TreeModel> {

    private IModel<Resource> currentResource;

    private static final ResourceComparator RESOURCE_COMPARATOR = new ResourceComparator();

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

        Collections.sort(resources, RESOURCE_COMPARATOR);

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

    private final static class ResourceComparator implements Comparator<Resource> {

        @Override
        public int compare(Resource o1, Resource o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

}
