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
package org.onexus.collection.manager.internal;

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.TaskStatus;
import org.onexus.collection.api.query.Query;

/**
 * <p>
 * This is an empty IEntityTable.
 * </p>
 * <p/>
 * <p>
 * Usually it's used when there is no result to return because it's still on the
 * ITaskManager. From here the user will be able to get the TaskStatus and keep
 * asking until the TaskStatus is done.
 * </p>
 *
 * @author Jordi Deu-Pons
 */
public class EmptyEntityTable implements IEntityTable {

    private Query query;
    private TaskStatus taskStatus;

    public EmptyEntityTable(Query query, TaskStatus taskStatus) {
        super();
        this.query = query;
        this.taskStatus = taskStatus;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public IEntity getEntity(String collectionURI) {
        return null;
    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public long size() {
        return 0;
    }

}
