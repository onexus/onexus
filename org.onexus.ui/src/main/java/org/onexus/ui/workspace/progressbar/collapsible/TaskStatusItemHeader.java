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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.TaskStatus;

/**
 * TaskStatusItemHeader is a way to represent given a task in concret, its state
 * in a fancy way.
 *
 * @author armand
 */
public class TaskStatusItemHeader extends Panel {

    private final ResourceReference img_refreshing = new PackageResourceReference(
            TaskStatusItemHeader.class, "ajax-loader2.gif");
    private final ResourceReference img_ok = new PackageResourceReference(
            TaskStatusItemHeader.class, "dialog-ok-3.png");

    public TaskStatusItemHeader(String id, IModel<TaskStatus> taskStatusModel) {
        super(id, taskStatusModel);

        TaskStatus task = taskStatusModel.getObject();
        add(new Label("itemName", task.getTitle()));
        String msg = "In process ...";
        if (task.isDone()) {
            msg = "OK!";
        } else if (task.isCancelled()) {
            msg = "Cancelled";
        }
        add(new Label("itemStatus", msg));
        add(new Image("statusimg", new ImageTaskStatusModel(
                new PropertyModel<Boolean>(taskStatusModel, "isDone"))));

    }

    private class ImageTaskStatusModel extends Model<ResourceReference> {

        IModel<Boolean> statusDoned; // True or False

        public ImageTaskStatusModel(IModel<Boolean> isDonedTaskModel) {
            this.statusDoned = isDonedTaskModel;
        }

        @Override
        public ResourceReference getObject() {
            // Returns kind of icon
            if (statusDoned.getObject()) {
                return img_ok;
            } else {
                return img_refreshing;
            }
        }
    }

}
