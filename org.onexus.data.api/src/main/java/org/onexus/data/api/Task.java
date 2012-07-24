package org.onexus.data.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {

    private String id;
    private Logger logger;
    private boolean canceled;
    private boolean done;

    private List<Task> subTasks = new ArrayList<Task>();

    public Task(String id) {
        this(id, new Logger());
    }

    public Task(String id, Logger logger) {
        this.id = id;
        this.logger = logger;
    }

    public String getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isDone() {
        return done && areSubTasksDone();
    }

    private boolean areSubTasksDone() {
        for (Task subTask : subTasks) {
            if (!subTask.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void addSubTask(Task task) {
        this.subTasks.add(task);
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }
}
