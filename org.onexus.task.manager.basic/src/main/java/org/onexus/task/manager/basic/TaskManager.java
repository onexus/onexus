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
package org.onexus.task.manager.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.onexus.core.IEntitySet;
import org.onexus.core.ITaskCallable;
import org.onexus.core.ITaskExecutor;
import org.onexus.core.ITaskManager;
import org.onexus.core.TaskStatus;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager implements ITaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

    private List<ITaskExecutor> executors;

    private int timeout = 50;

    private int maxThreads = 2;

    private Map<String, ITaskCallable> taskCallables;
    private Map<String, FutureTask<IEntitySet>> taskFutures;
    private ExecutorService executorService;

    public TaskManager() {
	super();
	this.taskCallables = Collections.synchronizedMap(new HashMap<String, ITaskCallable>());
	this.taskFutures = Collections.synchronizedMap(new HashMap<String, FutureTask<IEntitySet>>());
	this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void init() {
	if (executorService instanceof ThreadPoolExecutor) {
	    ((ThreadPoolExecutor) executorService).setMaximumPoolSize(maxThreads);
	}
    }

    @Override
    public TaskStatus submitCollection(Collection collection) {
	LOGGER.debug("Submiting collection {}", collection.getURI());

	Task task = collection.getTask();

	ITaskExecutor executor = getToolExecutor(task.getToolURI());
	ITaskCallable taskCall = executor.createCallable(collection);
	FutureTask<IEntitySet> taskFuture = new FutureTask<IEntitySet>(taskCall);
	executorService.submit(taskFuture);

	String taskId = taskCall.getStatus().getId();
	taskCallables.put(taskId, taskCall);
	taskFutures.put(taskId, taskFuture);

	return taskCall.getStatus();
    }

    @Override
    public boolean preprocessCollection(Collection collection) {

	Task task = collection.getTask();
	ITaskExecutor executor = getToolExecutor(task.getToolURI());

	return executor.preprocessCollection(collection);
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {

	if (!taskCallables.containsKey(taskId)) {
	    throw new RuntimeException("Unmanaged task '" + taskId + "'");
	}

	return taskCallables.get(taskId).getStatus();
    }

    @Override
    public IEntitySet getTaskOutput(String taskId) {
	LOGGER.debug("Getting task {} output", taskId);

	TaskStatus taskStatus = getTaskStatus(taskId);

	if (!taskStatus.isDone()) {
	    throw new RuntimeException("The task is still running.");
	}

	try {
	    return taskFutures.get(taskId).get(timeout, TimeUnit.SECONDS);
	} catch (Exception e) {
	    String msg = "Exception getting the task '" + taskId + "' output.";
	    LOGGER.error(msg, e);
	    throw new RuntimeException(msg, e);
	} finally {
	    taskFutures.remove(taskId);
	    taskCallables.remove(taskId);
	}
    }

    private ITaskExecutor getToolExecutor(String toolURI) {
	for (ITaskExecutor executor : executors) {
	    if (executor.isCallable(toolURI)) {
		return executor;
	    }
	}

	// TODO Auto-install tool

	String msg = "Tool executor for tool '" + toolURI + "' not found.";
	throw new RuntimeException(msg);
    }

    public List<ITaskExecutor> getExecutors() {
	return executors;
    }

    public void setExecutors(List<ITaskExecutor> executors) {
	this.executors = executors;
    }

    public int getTimeout() {
	return timeout;
    }

    public void setTimeout(int timeout) {
	this.timeout = timeout;
    }

    public int getMaxThreads() {
	return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
	this.maxThreads = maxThreads;
    }

}
