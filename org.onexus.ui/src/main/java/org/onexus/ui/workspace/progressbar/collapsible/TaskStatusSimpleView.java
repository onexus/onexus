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
package org.onexus.ui.workspace.progressbar.collapsible;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.TaskStatus;

/**
 * TaskStatusSimpleView is a recursive panel that visualizes the state of parent
 * task, and its childs.
 *
 * @author armand
 */
public class TaskStatusSimpleView extends Panel {

    public TaskStatusSimpleView(String id, final IModel<TaskStatus> taskStatusModel) {
        super(id);
        setOutputMarkupId(true);
        TaskStatusSimpleViewBox box = null;
        add(box = new TaskStatusSimpleViewBox("taskbox_simpleview",
                taskStatusModel));
        box.setOutputMarkupPlaceholderTag(true);

        RepeatingView rep = new RepeatingView("tasklist_simpleview");
        TaskStatus task = taskStatusModel.getObject();
        for (TaskStatus taskChild : task.getSubTasks()) {
            if (taskChild != null && taskChild.getId() != null) {
                rep.add(new TaskStatusSimpleView(rep.newChildId(), Model
                        .of(taskChild)));
            } else {
                rep.add(new EmptyPanel(rep.newChildId()));
            }
        }
        box.getBodyContainer().add(rep);
    }

}
