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
package org.onexus.ui.core.progressbar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.resource.api.TaskStatus;
import org.onexus.ui.core.utils.panels.icons.Icons;

import java.util.List;

/**
 * TaskStatusWidget contains a simple box to visualize the state of simultaneous
 * processes.
 *
 * @author armand *
 */
public class ProgressBarPanel extends Panel {

    public ProgressBarPanel(String id) {
        super(id);

        ListView<TaskStatus> listTaskStatus = new ListView<TaskStatus>("tasklist", new ListActiveTasksModel()) {
            @Override
            protected void populateItem(ListItem<TaskStatus> item) {
                if (item.getModelObject() != null) {
                    item.add(new Label("title", new PropertyModel<String>( item.getModel(), "title")));
                    item.add(new Image("progress", Icons.LOADING));
                }
            }
        };
        add(listTaskStatus);

    }

    private static class ListActiveTasksModel extends AbstractReadOnlyModel<List<TaskStatus>> {

        private transient List<TaskStatus> activeTasks;

        @Override
        public List<TaskStatus> getObject() {
            if (activeTasks == null) {
                activeTasks = ProgressBar.getActiveTasks().getActiveTasks();
            }
            return activeTasks;
        }

        @Override
        public void detach() {
            activeTasks = null;
        }
    }

}
