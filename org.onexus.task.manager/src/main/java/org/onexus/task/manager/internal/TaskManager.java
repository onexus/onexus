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
package org.onexus.task.manager.internal;

import org.onexus.resource.api.*;
import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Loader;
import org.onexus.resource.api.resources.Plugin;
import org.onexus.resource.api.resources.Project;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TaskManager implements ITaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

    private BundleContext context;

    private int timeout = 50;

    private int maxThreads = 2;

    private Map<String, ITask> taskCallables;
    private Map<String, FutureTask<IEntitySet>> taskFutures;
    private ExecutorService executorService;



    public TaskManager() {
        super();
        this.taskCallables = Collections.synchronizedMap(new HashMap<String, ITask>());
        this.taskFutures = Collections.synchronizedMap(new HashMap<String, FutureTask<IEntitySet>>());
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void init() {
        if (executorService instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executorService).setMaximumPoolSize(maxThreads);
        }
    }

    @Override
    public TaskStatus submitCollection(Project project, Collection collection) {
        LOGGER.debug("Submiting collection {}", collection.getURI());

        Loader loader = collection.getLoader();

        ILoader executor = getLoader(project, loader);
        ITask taskCall = executor.createCallable(project, collection);
        FutureTask<IEntitySet> taskFuture = new FutureTask<IEntitySet>(taskCall);
        executorService.submit(taskFuture);

        String taskId = taskCall.getStatus().getId();
        taskCallables.put(taskId, taskCall);
        taskFutures.put(taskId, taskFuture);

        return taskCall.getStatus();
    }

    @Override
    public boolean preprocessCollection(Project project, Collection collection) {

        Loader loader = collection.getLoader();
        ILoader executor = getLoader(project, loader);

        return executor.preprocessCollection(project, collection);
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

    private ILoader getLoader(Project project, Loader loader) {

        Plugin plugin = project.getPlugin(loader.getPlugin());

        if (plugin == null || plugin.getLocation() == null) {
            String msg = "Plugin '" + loader.getPlugin() + "' not defined in project " + project.getURI();
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        String pluginLocation = plugin.getLocation();

        try {
            for (ServiceReference service : context.getServiceReferences(ILoader.class.getName(), null)) {

                Bundle bundle = service.getBundle();

                if (bundle == null) {
                    continue;
                }

                if (pluginLocation.equals(bundle.getLocation())) {
                    LOGGER.info("Using bundle " + bundle.getBundleId() + " to execute " + loader.getPlugin());
                    return (ILoader) context.getService(service);
                }

            }
        } catch (InvalidSyntaxException e) {
            LOGGER.error("On context.getServiceReferences()", e);
        }

        // TODO Auto-install tool
        String msg = "Plugin for '" + loader + "' not found.";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
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
