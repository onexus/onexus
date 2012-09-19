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
package org.onexus.data.loader.biomart.internal;

import org.onexus.data.api.Data;
import org.onexus.data.api.IDataLoader;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;
import org.onexus.resource.api.Plugin;

import java.util.concurrent.Callable;

public class BiomartLoader implements IDataLoader {

    private String defaultMartService;

    /**
     * Initialization method called when configuration is updated
     */
    public void init() {

    }

    public String getDefaultMartService() {
        return defaultMartService;
    }

    public void setDefaultMartService(String defaultMartService) {
        this.defaultMartService = defaultMartService;
    }

    @Override
    public Callable<IDataStreams> newCallable(Progress progress, Plugin plugin, Data data) {
        return new BiomartCallable(progress, new BiomartRequest(data, defaultMartService));
    }

    @Override
    public long size() {
        return -1;
    }

}
