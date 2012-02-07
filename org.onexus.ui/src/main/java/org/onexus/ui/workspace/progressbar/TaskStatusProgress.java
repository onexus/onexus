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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import org.onexus.core.TaskStatus;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.utils.panels.icons.Icons;

public class TaskStatusProgress extends Panel {
    
    public final static PackageResourceReference CSS = new PackageResourceReference(TaskStatusProgress.class, "TaskStatusProgress.css");

    public final static MetaDataKey<ActiveTasks> TASKS = new MetaDataKey<ActiveTasks>() {};
    
    private final TaskStatusProgressBehavior REFRESH_PROGRESS_BAR = new TaskStatusProgressBehavior(Duration.seconds(5.0));
    
    private boolean open = false;
    private boolean runInBackground = false;
    
    public TaskStatusProgress(String id) {
	this(id, true);
    }
    
    public TaskStatusProgress(String id, boolean autoRefresh) {
	
	super(id);
	
	// Update the progress bar every 5 seconds
	setOutputMarkupId(true);
	
	if (autoRefresh) {
	    add(REFRESH_PROGRESS_BAR);
	    REFRESH_PROGRESS_BAR.start();
	}
	
	// Container DIV
	WebMarkupContainer container = new WebMarkupContainer("container") {

	    @Override
	    public boolean isVisible() {
		return (open = getActiveTasks().isActive());
	    }
	    
	};
	
	// Modal CSS window
	final WebMarkupContainer modal = new WebMarkupContainer("modal") {
	    @Override
	    public boolean isVisible() {
		return (open = getActiveTasks().isActive()) && !runInBackground;
	    }
	};
	
	modal.add(new TaskStatusProgressDetailsPanel("progressDetails") {

	    @Override
	    protected void onBackground(AjaxRequestTarget target) {
		runInBackground = true;
		//target.add(TaskStatusProgress.this);
	    }
	    
	});
	add( modal );
		
	// Prepare panel	
	WebMarkupContainer activeTasks = new WebMarkupContainer("InfoActiveTasks");
	activeTasks.add(new Label("quickInfoTasks", "Several tasks running ..."));
	AjaxLink<String> viewTasksLink = new AjaxLink<String>("viewInfoTasks") {
	    @Override
	    public void onClick(AjaxRequestTarget target) {
		runInBackground = false;
	    }	   
	};
	
	viewTasksLink.add(new ImageWithoutAntiCache("refreshbar", new PackageResourceReference(
		TaskStatusProgress.class, "ajax-loader.gif")));

	viewTasksLink.add(new ImageWithoutAntiCache("infobutton", Icons.INFORMATION));
	
	activeTasks.add(viewTasksLink);
	container.add(activeTasks);	
	
	add(container);

    }
    
    @Override
    public void renderHead(IHeaderResponse response) {
	super.renderHead(response);
	response.renderCSSReference(CSS);
    }

    @Override
    public void onEvent(IEvent<?> event) {
	
	if (event.getPayload() instanceof AjaxRequestTarget) {
	    AjaxRequestTarget target = ((AjaxRequestTarget) event.getPayload());
	    
	    target.add(this);
	    
	    if (open && !getActiveTasks().isActive() ) {
		send(getPage(), Broadcast.BREADTH, EventViewChange.EVENT);
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
    
    public static class ActiveTasks implements Serializable {

	private List<TaskStatus> tasks = new ArrayList<TaskStatus>();

	public void addTask(TaskStatus task) {
	    tasks.add(task);
	}

	public boolean isActive() {
	    boolean res = false;
	    for (TaskStatus task : tasks) {
		if (!task.isDone()) {
		    res = true;
		    break;
		}
	    }
	    return res;
	}

	public List<TaskStatus> getTasks() {
	    List<TaskStatus> activeTasks = new ArrayList<TaskStatus>();
	    for (TaskStatus task : tasks) {
		activeTasks.add(task);
	    }

	    this.tasks = activeTasks;
	    return this.tasks;
	}

	public List<TaskStatus> getActiveTasks() {
	    List<TaskStatus> activeTasks = new ArrayList<TaskStatus>();
	    for (TaskStatus task : tasks) {
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
