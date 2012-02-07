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
package org.onexus.ui.workspace.progressbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.onexus.core.TaskStatus;

import java.util.List;

/**
 * TaskStatusWidget contains a simple box to visualize the state of simultaneous
 * processes.
 *
 * @author armand *
 */
public abstract class TaskStatusProgressDetailsPanel extends Panel {

    public TaskStatusProgressDetailsPanel(String id) {
        super(id);

        add(new AjaxLink<String>("close") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onBackground(target);
            }

            @Override
            public boolean isVisible() {
                // By now is not properly tested
                return false;
            }

        });

        ListView<TaskStatus> listTaskStatus = new ListView<TaskStatus>(
                "tasklist", new ListActiveTasksModel()) {
            @Override
            protected void populateItem(ListItem<TaskStatus> item) {
                if (item.getModelObject() != null) {
                    item.add(new TaskStatusPanel("taskitem", item.getModel()));
                }
            }
        };
        add(listTaskStatus);

    }

    private static class ListActiveTasksModel extends
            AbstractReadOnlyModel<List<TaskStatus>> {

        @Override
        public List<TaskStatus> getObject() {
            return TaskStatusProgress.getActiveTasks().getActiveTasks();
        }

    }

    protected abstract void onBackground(AjaxRequestTarget target);

}
