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
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import org.onexus.data.api.IProgressable;
import org.onexus.data.api.Progress;
import org.onexus.ui.api.utils.panels.icons.Icons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgressBar extends Panel {
    public final static PackageResourceReference CSS = new PackageResourceReference(ProgressBar.class, "ProgressBar.css");
    public final static MetaDataKey<ActiveProgress> TASKS = new MetaDataKey<ActiveProgress>() {};
    private boolean open = false;

    public ProgressBar(String id) {
        super(id);

        setOutputMarkupId(true);

        final WebMarkupContainer modal = new WebMarkupContainer("modal") {
            @Override
            public boolean isVisible() {
                return (open = getActiveProgress().isActive());
            }
        };

        modal.add(new AjaxLink<String>("refresh") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(ProgressBar.this);
            }
        });

        ListView<Progress> listTaskStatus = new ListView<Progress>("tasklist", new ListActiveTasksModel()) {
            @Override
            protected void populateItem(ListItem<Progress> item) {
                if (item.getModelObject() != null) {
                    item.add(new Label("title", new PropertyModel<String>( item.getModel(), "name")));
                    item.add(new Image("progress", Icons.LOADING));
                }
            }
        };
        modal.add(listTaskStatus);
        modal.setMarkupId("progressbar-modal");

        add(modal);

    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    /*
    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = ((AjaxRequestTarget) event.getPayload());

            if (open || getActiveProgress().isActive()) {
                open = true;
                target.add(this);
            }

            if (open && !getActiveProgress().isActive()) {
                open = false;
                //TODO send(getPage(), Broadcast.BREADTH, EventViewChange.EVENT);
            }
        }
    }
    */

    public static ActiveProgress getActiveProgress() {
        ActiveProgress progress = Session.get().getMetaData(TASKS);
        if (progress == null) {
            progress = new ActiveProgress();
            Session.get().setMetaData(TASKS, progress);
        }
        return progress;
    }

    public static <T extends IProgressable> T show(T progressable) {

        Progress progress = progressable.getProgress();
        if (progress != null) {
            getActiveProgress().getProgresses().add(progress);
        }

        return progressable;
    }

    public static class ActiveProgress implements Serializable {

        private List<Progress> progresses = new ArrayList<Progress>();

        public void addTask(Progress progress) {
            progresses.add(progress);
        }

        public boolean isActive() {
            boolean res = false;
            for (Progress progress : progresses) {
                if (!progress.isDone()) {
                    res = true;
                    break;
                }
            }
            return res;
        }

        public List<Progress> getProgresses() {
            List<Progress> activeProgresses = new ArrayList<Progress>();
            for (Progress progress : progresses) {
                activeProgresses.add(progress);
            }

            this.progresses = activeProgresses;
            return this.progresses;
        }

        public List<Progress> getActiveTasks() {
            List<Progress> activeProgresses = new ArrayList<Progress>();
            for (Progress progress : progresses) {
                if (!progress.isDone()) {
                    activeProgresses.add(progress);
                }
            }

            this.progresses = activeProgresses;
            return this.progresses;
        }

        @Override
        public String toString() {
            return getActiveTasks().toString();
        }

    }

    private static class ListActiveTasksModel extends AbstractReadOnlyModel<List<Progress>> {

        private transient List<Progress> activeProgresses;

        @Override
        public List<Progress> getObject() {
            if (activeProgresses == null) {
                activeProgresses = ProgressBar.getActiveProgress().getActiveTasks();
            }
            return activeProgresses;
        }

        @Override
        public void detach() {
            activeProgresses = null;
        }
    }


}
