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
package org.onexus.loader.biomart.internal;

import org.onexus.collection.api.ILoader;
import org.onexus.collection.api.ITask;
import org.onexus.collection.api.Collection;
import org.onexus.resource.api.Project;

public class TaskExecutor implements ILoader {

    private String defaultMartService;

    private String defaultVirtualSchema;

    /**
     * Initialization method called when configuration is updated
     */
    public void init() {

    }

    @Override
    public ITask createCallable(Project project, Collection collection) {
        return new TaskCallable(new BiomartRequest(collection, defaultMartService, defaultVirtualSchema));
    }

    @Override
    public boolean preprocessCollection(Project project, Collection collection) {
        return false;
    }

    public String getDefaultMartService() {
        return defaultMartService;
    }

    public void setDefaultMartService(String defaultMartService) {
        this.defaultMartService = defaultMartService;
    }

    public String getDefaultVirtualSchema() {
        return defaultVirtualSchema;
    }

    public void setDefaultVirtualSchema(String defaultVirtualSchema) {
        this.defaultVirtualSchema = defaultVirtualSchema;
    }

}
