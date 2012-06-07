/**
 *  Copyright 2012 Universitat Pompeu Fabra.
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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.tree.BaseTree;
import org.apache.wicket.extensions.markup.html.tree.ITreeState;
import org.apache.wicket.extensions.markup.html.tree.LinkTree;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceManager.ResourceStatus;
import org.onexus.core.resources.Resource;
import org.onexus.ui.workspace.events.EventResourceSelect;
import org.onexus.ui.workspace.events.EventResourceSync;

import javax.inject.Inject;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class ProjectTree extends LinkTree {

    @Inject
    private IResourceManager resourceManager;

    private IModel<Resource> currentResource;

    public ProjectTree(String id, IModel<Resource> currentResource) {
        super(id, new ProjectTreeModel(currentResource));
        setOutputMarkupId(true);

        this.currentResource = currentResource;

        setRootLess(false);

        selectCurrentResource();

    }

    private void selectCurrentResource() {
        Resource resource = currentResource.getObject();
        if (resource != null) {
            ITreeState treeState = getTreeState();
            DefaultMutableTreeNode node = ResourceNode.get(resource);
            expandRecursive(treeState, node);
            treeState.selectNode(node, true);
        }
    }

    private static void expandRecursive(ITreeState treeState, TreeNode node) {

        TreeNode parent = node.getParent();
        if (parent != null) {
            expandRecursive(treeState, parent);
        }

        treeState.expandNode(node);

    }

    @SuppressWarnings("unchecked")
    @Override
    protected IModel<?> getNodeTextModel(IModel<?> nodeModel) {
        return new DirtyModel((IModel<DefaultMutableTreeNode>) nodeModel);
    }

    @Override
    protected void onNodeLinkClicked(Object node, BaseTree tree, AjaxRequestTarget target) {

        ResourceNode resourceNode = (ResourceNode) ((DefaultMutableTreeNode) node).getUserObject();

        String resourceURI = resourceNode.getUri();

        Resource resource = resourceManager.load(Resource.class, resourceURI);

        currentResource.setObject(resource);

        send(getPage(), Broadcast.BREADTH, EventResourceSelect.EVENT);
    }

    @Override
    public void onEvent(IEvent<?> event) {

        Object payLoad = event.getPayload();

        if (payLoad == null) {
            return;
        }

        if (EventResourceSync.EVENT == payLoad) {
            updateAJAX(this);
        }

    }

    private static void updateAJAX(Component component) {
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target != null) {
            target.add(component);
        }
    }

    private class DirtyModel extends AbstractReadOnlyModel<String> {

        private IModel<DefaultMutableTreeNode> innerModel;

        public DirtyModel(IModel<DefaultMutableTreeNode> nodeModel) {
            super();
            this.innerModel = nodeModel;
        }

        @Override
        public String getObject() {

            DefaultMutableTreeNode node = innerModel.getObject();

            if (node == null) {
                return null;
            }

            ResourceNode resourceNode = (ResourceNode) node.getUserObject();

            if (resourceNode == null) {
                return null;
            }

            ResourceStatus status = resourceManager.status(resourceNode.getUri());

            String prefix = "";
            switch (status) {
                case ADD:
                    prefix = "(A) ";
                    break;
                case REMOVE:
                    prefix = "(D) ";
                    break;
                case UPDATE:
                    prefix = "(*) ";
                    break;
            }

            return prefix + resourceNode.toString();

        }

    }

}
