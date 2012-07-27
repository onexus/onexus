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
import java.util.ArrayList;
import java.util.List;

public class Progress implements Serializable {

    private String id;
    private Logger logger;
    private boolean canceled;
    private boolean done;

    private List<Progress> subProgresses = new ArrayList<Progress>();

    public Progress(String id) {
        this(id, new Logger());
    }

    public Progress(String id, Logger logger) {
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
        for (Progress subProgress : subProgresses) {
            if (!subProgress.isDone()) {
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

    public void addSubTask(Progress progress) {
        this.subProgresses.add(progress);
    }

    public List<Progress> getSubProgresses() {
        return subProgresses;
    }
}
