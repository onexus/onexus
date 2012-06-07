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
package org.onexus.ui.workspace.pages.tools;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Project;
import org.onexus.ui.workspace.pages.ResourcesPage;

import javax.inject.Inject;
import java.util.List;

public class ProjectSelectorTool extends AbstractTool<Project> {

    @Inject
    private IResourceManager resourceManager;

    public ProjectSelectorTool() {
        super(new Model<Project>());

        add(new DropDownChoice<Project>("resources-toolbar-selector", getModel(), new WorkspacesListModel(), new WorkspaceRenderer()) {

            @Override
            protected void onSelectionChanged(Project newSelection) {

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

    private class WorkspacesListModel extends AbstractReadOnlyModel<List<Project>> {

        @Override
        public List<Project> getObject() {

            List<Project> workspaces = resourceManager.loadChildren(Project.class, null);
            return workspaces;

        }

    }

    private class WorkspaceRenderer implements IChoiceRenderer<Project> {

        @Override
        public Object getDisplayValue(Project workspace) {
            return workspace.getName();
        }

        @Override
        public String getIdValue(Project object, int index) {
            return Integer.toString(index);
        }

    }

}
