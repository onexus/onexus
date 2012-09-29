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
package org.onexus.data.api;

import java.io.Serializable;
import java.util.*;

public class Progress implements Serializable {

    public enum LogType {DEBUG, WARNING, INFO, ERROR}

    public enum Status {WAITING, RUNNING, FAILED, CANCELED, DONE}

    private String id;
    private String title;
    private Status status;

    private LinkedList<LogMsg> logs = new LinkedList<LogMsg>();
    private LinkedList<Progress> subProgresses = new LinkedList<Progress>();

    public Progress(String id, String title) {
        this.id = id;
        this.title = title;
        this.status = null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void waiting() {
        setStatus(Status.WAITING);
    }

    public void run() {
        setStatus(Status.RUNNING);
    }

    public void fail() {
        setStatus(Status.FAILED);
    }

    public void done() {
        setStatus(Status.DONE);
    }

    public Progress error(String msg) {
        logs.add(new LogMsg(LogType.ERROR, msg));
        return this;
    }

    public Progress info(String msg) {
        logs.add(new LogMsg(LogType.INFO, msg));
        return this;
    }

    public Progress warning(String msg) {
        logs.add(new LogMsg(LogType.WARNING, msg));
        return this;
    }

    public Progress debug(String msg) {
        logs.add(new LogMsg(LogType.DEBUG, msg));
        return this;
    }

    public List<LogMsg> getLogs() {
        return logs;
    }

    public boolean isAborted() {
        return status == Status.FAILED ||
                status == Status.CANCELED;
    }

    public boolean isDone() {
        Status status = getStatus();
        return status != Status.WAITING &&
                status != Status.RUNNING;
    }

    private boolean areSubTasksDone() {
        for (Progress subProgress : subProgresses) {
            if (!subProgress.isDone()) {
                return false;
            }
        }
        return true;
    }

    public Status getStatus() {
        if (status == null) {

            if (subProgresses.isEmpty()) {
                return Status.WAITING;
            } else {
                for (Progress subProgress : subProgresses) {
                    if (subProgress.getStatus() == Status.FAILED) {
                        return Status.FAILED;
                    }

                    if (subProgress.getStatus() == Status.CANCELED) {
                        return Status.CANCELED;
                    }

                    if (subProgress.getStatus() != Status.DONE) {
                        return Status.RUNNING;
                    }
                }
                return Status.DONE;
            }
        }

        return status;
    }

    public Progress setStatus(Status status) {
        info("Task status " + status.toString());
        this.status = status;
        return this;
    }

    public Progress addSubTask(Progress progress) {
        this.subProgresses.addFirst(progress);
        return this;
    }

    public List<Progress> getSubProgresses() {
        return subProgresses;
    }

    @Override
    public String toString() {
        return id;
    }


    public class LogMsg implements Serializable {

        private Date time;
        private String message;
        private LogType type;

        public LogMsg(LogType type, String message) {
            this.time = new Date();
            this.type = type;
            this.message = message;
        }

        public Date getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LogType getType() {
            return type;
        }

        public void setType(LogType type) {
            this.type = type;
        }
    }

}
