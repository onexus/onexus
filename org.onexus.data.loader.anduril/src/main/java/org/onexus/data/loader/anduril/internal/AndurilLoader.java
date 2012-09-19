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
package org.onexus.data.loader.anduril.internal;

import fi.helsinki.ltdk.csbl.anduril.core.network.Repository;
import fi.helsinki.ltdk.csbl.anduril.core.network.launcher.JavaLauncher;
import org.onexus.data.api.*;
import org.onexus.resource.api.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class AndurilLoader implements IDataLoader {

    private String andurilHome;

    private String executionDir;

    private String heapSize;

    private Repository repository;

    public AndurilLoader() {
        super();
    }

    public void init() {
        JavaLauncher.setHeapSize(Integer.valueOf(heapSize));

        try {
            repository = new Repository(new File(andurilHome));
            repository.load(true, null, null, null);
            if (repository.hasErrors()) {
                throw new IOException("Error loading Anduril repository");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAndurilHome() {
        return andurilHome;
    }

    public void setAndurilHome(String andurilHome) {
        this.andurilHome = andurilHome;
    }

    public String getExecutionDir() {
        return executionDir;
    }

    public void setExecutionDir(String executionDir) {
        this.executionDir = executionDir;
    }

    public String getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(String heapSize) {
        this.heapSize = heapSize;
    }


    @Override
    public Callable<IDataStreams> newCallable(Progress progress, Plugin plugin, Data data) {
        return new AndurilCallable(progress, repository, executionDir, data);
    }

    @Override
    public long size() {
        return -1;
    }
}
