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
package org.onexus.task.executor.loader.biomart;

import org.onexus.core.ITaskCallable;
import org.onexus.core.ITaskExecutor;
import org.onexus.core.resources.Collection;

public class TaskExecutor implements ITaskExecutor {

    private String defaultMartService;

    private String defaultVirtualSchema;

    @Override
    public boolean isCallable(String toolURI) {
	return "http://www.onexus.org/tools/loader-biomart/1.0.0".equals(toolURI);
    }
    
    /**
     * Initialization method called when configuration is updated
     */
    public void init() {
	
    }

    @Override
    public ITaskCallable createCallable(Collection collection) {
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
