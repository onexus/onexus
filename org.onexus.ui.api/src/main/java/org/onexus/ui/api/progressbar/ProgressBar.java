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
package org.onexus.ui.api.progressbar;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import org.onexus.collection.api.IEntityTable;
import org.onexus.data.api.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgressBar extends Panel {
    public final static PackageResourceReference CSS = new PackageResourceReference(ProgressBar.class, "ProgressBar.css");
    public final static MetaDataKey<ActiveTasks> TASKS = new MetaDataKey<ActiveTasks>() {
    };

    private boolean open = false;

    private boolean usePrecondition = false;

    public ProgressBar(String id, boolean usePrecondition) {
        super(id);

        this.usePrecondition = usePrecondition;

        setOutputMarkupId(true);
        // add(new Refresh());

        final WebMarkupContainer modal = new WebMarkupContainer("modal") {
            @Override
            public boolean isVisible() {
                return (open = getActiveTasks().isActive());
            }
        };
        modal.setMarkupId("progressbar-modal");
        modal.add(new ProgressBarPanel("progressDetails"));
        add(modal);

    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = ((AjaxRequestTarget) event.getPayload());

            if (open || getActiveTasks().isActive()) {
                open = true;
                target.add(this);
            }

            if (open && !getActiveTasks().isActive()) {
                open = false;
                //TODO send(getPage(), Broadcast.BREADTH, EventViewChange.EVENT);
            }
        }
    }

    private class Refresh extends AbstractAjaxTimerBehavior {

        public Refresh() {
            super(Duration.seconds(3));
        }

        @Override
        protected void onTimer(AjaxRequestTarget target) {
            // Nothing adding throw onEvent
        }


        @Override
        protected CharSequence getPreconditionScript() {
            if (usePrecondition) {
                String componentId = "progressbar-modal";
                return "var c = Wicket.$('" + componentId + "'); return typeof(c) != 'undefined' && c != null";
            } else {
                return super.getPreconditionScript();
            }
        }

    }

    public static ActiveTasks getActiveTasks() {
        ActiveTasks tasks = Session.get().getMetaData(TASKS);
        if (tasks == null) {
            tasks = new ActiveTasks();
            Session.get().setMetaData(TASKS, tasks);
        }
        return tasks;
    }

    public static IEntityTable show(IEntityTable entityTable) {

        Task status = entityTable.getTask();
        if (status != null) {
            getActiveTasks().getTasks().addAll(status.getSubTasks());
        }

        return entityTable;
    }

    public static class ActiveTasks implements Serializable {

        private List<Task> tasks = new ArrayList<Task>();

        public void addTask(Task task) {
            tasks.add(task);
        }

        public boolean isActive() {
            boolean res = false;
            for (Task task : tasks) {
                if (!task.isDone()) {
                    res = true;
                    break;
                }
            }
            return res;
        }

        public List<Task> getTasks() {
            List<Task> activeTasks = new ArrayList<Task>();
            for (Task task : tasks) {
                activeTasks.add(task);
            }

            this.tasks = activeTasks;
            return this.tasks;
        }

        public List<Task> getActiveTasks() {
            List<Task> activeTasks = new ArrayList<Task>();
            for (Task task : tasks) {
                if (!task.isDone()) {
                    activeTasks.add(task);
                }
            }

            this.tasks = activeTasks;
            return this.tasks;
        }

        @Override
        public String toString() {
            return getActiveTasks().toString();
        }

    }

}
