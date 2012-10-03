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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultTableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ISortableTreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onexus.data.api.IProgressable;
import org.onexus.data.api.Progress;
import org.onexus.ui.api.progressbar.columns.LogsColumn;
import org.onexus.ui.api.progressbar.columns.StatusColumn;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProgressBar extends Panel {
    public final static PackageResourceReference CSS = new PackageResourceReference(ProgressBar.class, "ProgressBar.css");
    public final static MetaDataKey<ActiveProgress> TASKS = new MetaDataKey<ActiveProgress>() {};

    private List<IColumn<Progress, String>> columns;
    private ProgressTreeProvider provider;
    private ProgressExpansionModel treeState;

    public ProgressBar(String id) {
        super(id);

        provider =  new ProgressTreeProvider();
        treeState = new ProgressExpansionModel();

        setOutputMarkupId(true);

        final WebMarkupContainer modal = new WebMarkupContainer("modal") {

            @Override
            protected void onBeforeRender() {
                addOrReplace(new DefaultTableTree<Progress, String>("tree", columns, provider, Integer.MAX_VALUE, treeState).setOutputMarkupId(true));
                super.onBeforeRender();
            }
        };

        final WebMarkupContainer minimized = new WebMarkupContainer("minimized");
        minimized.setOutputMarkupId(true);
        add(minimized);

        final WebMarkupContainer logDetails = new WebMarkupContainer("log-details");
        logDetails.setOutputMarkupId(true);
        logDetails.add(new EmptyPanel("logs"));
        modal.add(logDetails);

        modal.add(new AjaxLink<String>("refresh") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(modal.get("tree"));
            }
        });

        modal.setMarkupId("taskmanagerModal");
        add(modal);


        minimized.add(new AjaxLink<String>("show") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(modal);
                target.add(minimized);
                target.appendJavaScript("$('#taskmanagerModal').modal('show')");
            }
        });

        this.columns = new ArrayList<IColumn<Progress, String>>();
        columns.add(new TreeColumn<Progress, String>(Model.of("task")));
        columns.add(new PropertyColumn<Progress, String>(Model.of("title"), "title"));
        columns.add(new StatusColumn());
        columns.add(new LogsColumn() {

            @Override
            protected void showLogs(Progress progress, AjaxRequestTarget target) {
                logDetails.addOrReplace(new Label("logs", toLogsString(progress)));
                target.add(logDetails);
            }
        });

    }

    private static SimpleDateFormat formatter= new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");

    private static String toLogsString(Progress progress) {

        StringBuilder str = new StringBuilder();

        for (Progress.LogMsg msg : progress.getLogs()) {
            str.append(" [").append(msg.getType()).append("] ");
            str.append(formatter.format(msg.getTime())).append(" - ");
            str.append(msg.getMessage());
            str.append('\n');
        }

        return str.toString();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    private class ProgressTreeProvider implements ISortableTreeProvider<Progress, String> {

        @Override
        public Iterator<? extends Progress> getRoots() {
            Collection<Progress> progresses = getActiveProgress().getProgresses();
            List<Progress> orderProgresses = new ArrayList<Progress>(progresses);

            Collections.sort(orderProgresses, new Comparator<Progress>() {

                @Override
                public int compare(Progress o1, Progress o2) {

                    boolean d1 = o1.isDone();
                    boolean d2 = o2.isDone();

                    if (d1 && !d2) {
                        return 1;
                    }

                    if (!d1 && d2) {
                        return -1;
                    }

                    if (!d1 && !d2) {
                        if (o1.getStatus() == Progress.Status.RUNNING) {
                            return -1;
                        }

                        if (o2.getStatus() == Progress.Status.RUNNING) {
                            return 1;
                        }
                    }

                    return 0;
                }
            });

            return orderProgresses.iterator();
        }

        @Override
        public boolean hasChildren(Progress node) {
            return !node.getSubProgresses().isEmpty();
        }

        @Override
        public Iterator<? extends Progress> getChildren(Progress node) {
            return node.getSubProgresses().iterator();
        }

        @Override
        public IModel<Progress> model(Progress object) {
            return new Model<Progress>(object);
        }

        @Override
        public void detach() {

        }

        @Override
        public ISortState<String> getSortState() {
            return new SingleSortState<String>();
        }

    }

    private class ProgressExpansionModel extends AbstractReadOnlyModel<Set<Progress>>
    {
        @Override
        public Set<Progress> getObject()
        {
            return ProgressExpansion.get();
        }
    }

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
            if (!progress.getSubProgresses().isEmpty()) {
                for (Progress subprogress : progress.getSubProgresses()) {
                    getActiveProgress().addTask(subprogress);
                }
            } else {
                getActiveProgress().addTask(progress);
            }

            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
            if (target != null) {
                target.appendJavaScript("$('#taskmanagerModal').modal('show')");
            }
        }

        return progressable;
    }

    public static class ActiveProgress implements Serializable {

        private Map<String, Progress> progresses = new HashMap<String, Progress>();

        public void addTask(Progress progress) {
            progresses.put(progress.getId(), progress);
        }

        public boolean isActive() {
            boolean res = false;
            for (Progress progress : getProgresses()) {
                if (!progress.isDone()) {
                    res = true;
                    break;
                }
            }
            return res;
        }

        public Collection<Progress> getProgresses() {
            return progresses.values();
        }

    }

}
