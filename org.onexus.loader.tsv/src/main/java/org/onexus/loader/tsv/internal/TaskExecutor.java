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
package org.onexus.loader.tsv.internal;

import org.onexus.collection.api.*;
import org.onexus.data.api.IDataManager;
import org.onexus.collection.api.IDataLoader;
import org.onexus.resource.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaskExecutor implements IDataLoader {

    private IDataManager dataManager;

    public TaskExecutor() {
        super();
    }

    @Override
    public ITask createCallable(Project project, Collection collection) {
        return new TsvTaskCallable(dataManager, collection);
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

}
