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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.TaskStatus;


public class TaskStatusSimpleViewBox extends Border {

    @SuppressWarnings("unchecked")
    public TaskStatusSimpleViewBox(String id, IModel<TaskStatus> taskModel) {

        super(id, new Model<Boolean>());
        // I have to do it in ajax, otherwise i leave the page
        // setOutputMarkupId(true);
        setCollapsed(taskModel.getObject().isDone());

        final WebMarkupContainer body = new WebMarkupContainer(
                "collapsiblebody") {
            @Override
            protected void onConfigure() {
                setVisible(!isCollapsed());
            }
        };
        body.setOutputMarkupPlaceholderTag(true); // important
        addToBorder(body);
        body.add(getBodyContainer());
        AjaxLink<Boolean> toggle = new AjaxLink<Boolean>("toggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {

                setCollapsed(!isCollapsed());
                target.add(body);
                target.add(this);

            }
        };
        toggle.setOutputMarkupId(true);
        addToBorder(toggle);
        toggle.add(new ArrowToggleButton("toggleButton", (IModel<Boolean>) getDefaultModel()));
        addToBorder(new TaskStatusItemHeader("itemHeader", taskModel));
    }

    public void setCollapsed(boolean collapsed) {
        setDefaultModelObject(collapsed);
    }

    public boolean isCollapsed() {
        return Boolean.TRUE.equals(getDefaultModelObject());
    }

}
