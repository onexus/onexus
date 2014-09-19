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
package org.onexus.ui.workspace.internal.viewers.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.extensions.markup.html.repeater.util.ProviderSubset;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.ui.api.events.EventResourceSelect;

import java.util.Set;

public class ProjectTree extends NestedTree<Resource> {

    private IModel<Resource> selected;

    private Set<Resource> state;

    private static final ResourceReference CSS = new CssResourceReference(ProjectTree.class, "css/tree.css");

    public ProjectTree(String id, ITreeProvider<Resource> treeProvider, IModel<? extends Resource> selectedResource) {
        super(id, treeProvider);

        this.state = new ProviderSubset<Resource>(treeProvider, true);
        this.selected = (IModel<Resource>) selectedResource;

        setOutputMarkupId(true);
        setModel(newStateModel());
        add(new WindowsTheme());

    }

    protected boolean isSelected(Resource resource) {

        if (resource != null && selected.getObject() != null) {
            return resource.getORI().equals(selected.getObject().getORI());
        }

        return false;
    }


    protected void select(Resource resource, final AjaxRequestTarget target) {

        // Update unselected node
        updateNode(selected.getObject(), target);

        // Select new node
        selected.setObject(resource);

        // Update new selection
        updateNode(resource, target);

        send(getPage(), Broadcast.BREADTH, EventResourceSelect.EVENT);
    }

    private IModel<Set<Resource>> newStateModel() {
        return new AbstractReadOnlyModel<Set<Resource>>() {
            @Override
            public Set<Resource> getObject() {
                return state;
            }

            /**
             * Super class doesn't detach - would be nice though.
             */
            @Override
            public void detach() {
                ((IDetachable) state).detach();
            }
        };
    }

    @Override
    protected void onDetach() {
        if (selected != null) {
            selected.detach();
        }
        super.onDetach();
    }

    @Override
    protected Component newContentComponent(String id, IModel<Resource> model) {
        return new Folder<Resource>(id, this, model) {

            @Override
            protected boolean isClickable() {
                return true;
            }


            @Override
            protected void onClick(AjaxRequestTarget target) {
                ProjectTree.this.select(getModelObject(), target);
            }


            @Override
            protected boolean isSelected() {
                return ProjectTree.this.isSelected(getModelObject());
            }

            @Override
            protected IModel<?> newLabelModel(IModel<Resource> resourceIModel) {
                return new PropertyModel<String>(resourceIModel, "name");
            }

            @Override
            protected String getStyleClass() {

                Resource t = getModelObject();

                String styleClass;
                if (getProvider().hasChildren(t)) {
                    if (getState(t) == State.EXPANDED) {
                        if (t instanceof Project) {
                            styleClass = "tree-project-open";
                        } else {
                            styleClass = getOpenStyleClass();
                        }
                    } else {
                        if (t instanceof Project) {
                            styleClass = "tree-project-closed";
                        } else {
                            styleClass = getClosedStyleClass();
                        }
                    }
                } else {
                    styleClass = getOtherStyleClass(t);
                }

                if (isSelected()) {
                    styleClass += " " + getSelectedStyleClass();
                }

                return styleClass;
            }

            @Override
            protected String getOtherStyleClass(Resource resource) {
                return "resource-" + resource.getClass().getSimpleName().toLowerCase();
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
    }


}


