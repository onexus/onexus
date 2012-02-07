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
package org.onexus.core;

import java.io.Serializable;
import java.util.*;

public class TaskStatus implements Serializable {

    private String id;
    private String title;
    private boolean done = false;
    private boolean cancelled = false;

    private List<String> logs = new ArrayList<String>();
    private Map<String, TaskStatus> subTasks = new HashMap<String, TaskStatus>();

    public TaskStatus(String id, String title) {
        super();
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public void addLog(String msg) {
        this.logs.add(msg);
    }

    public Collection<TaskStatus> getSubTasks() {
        return subTasks.values();
    }

    public void addSubTask(TaskStatus task) {
        this.subTasks.put(task.getId(), task);
    }

    @Override
    public String toString() {
        return "TaskStatus [id=" + id + ", title=" + title + ", done=" + done
                + ", cancelled=" + cancelled + ", logs=" + logs + ", subTasks="
                + subTasks + "]";
    }

}
