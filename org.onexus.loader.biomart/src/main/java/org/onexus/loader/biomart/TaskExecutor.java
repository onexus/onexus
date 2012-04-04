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
package org.onexus.loader.biomart;

import org.onexus.core.ILoader;
import org.onexus.core.ITask;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Loader;

public class TaskExecutor implements ILoader {

    private String defaultMartService;

    private String defaultVirtualSchema;

    @Override
    public boolean isCallable(Loader loader) {
        return "mvn:org.onexus/org.onexus.loader.biomart/0.2".equals(loader.getPlugin());
    }

    /**
     * Initialization method called when configuration is updated
     */
    public void init() {

    }

    @Override
    public ITask createCallable(Collection collection) {
        return new TaskCallable(new BiomartRequest(collection, defaultMartService, defaultVirtualSchema));
    }

    @Override
    public boolean preprocessCollection(Collection collection) {
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
