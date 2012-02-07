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
package org.onexus.task.executor.anduril;

import fi.helsinki.ltdk.csbl.anduril.core.engine.DynamicError;
import fi.helsinki.ltdk.csbl.anduril.core.engine.NetworkEvaluator;
import fi.helsinki.ltdk.csbl.anduril.core.network.Repository;
import fi.helsinki.ltdk.csbl.anduril.core.network.componentInstance.OutputComponentInstance;
import org.onexus.core.IEntitySet;
import org.onexus.core.ITaskCallable;
import org.onexus.core.TaskStatus;
import org.onexus.core.resources.Collection;
import org.onexus.task.executor.loader.tsv.FileEntitySet;

import java.io.File;

public class TaskCallable implements ITaskCallable {

    private TaskStatus status;
    private Collection collection;
    private NetworkEvaluator evaluator;
    private String executionDir;

    public TaskCallable(Repository repository, String baseExecutionDir,
                        Collection collection) {
        super();

        String collectionURI = collection.getURI();
        String collectionHash = Long.toHexString(collectionURI.hashCode());
        this.collection = collection;
        this.status = new TaskStatus(collectionHash,
                "Preparing ANDURIL execution of '" + collectionURI + "'");

        this.executionDir = baseExecutionDir + File.separator + collectionHash;
        final File executionDirFile = new File(executionDir);
        if (!executionDirFile.exists()) {
            executionDirFile.mkdirs();
        }

        evaluator = new NetworkEvaluator(executionDirFile);
        String andurilScript = collection.getTask().getParameter("SCRIPT");
        evaluator.setSource(andurilScript, repository, repository.getSymbolTable());
        evaluator.parse();
        if (evaluator.hasErrors()) {
            //TODO
        }
    }

    @Override
    public IEntitySet call() throws Exception {
        status.addLog("Starting Anduril execution");
        evaluator.execute();

        if (evaluator.hasErrors()) {
            for (DynamicError error : evaluator.getEngineErrors()) {
                status.addLog(error.format());
            }
            status.setCancelled(true);
            status.setDone(true);
        }

        status.setDone(true);

        String outputDirectory = "file:" + executionDir + File.separator + OutputComponentInstance.OUTPUT_DIRECTORY;

        return new FileEntitySet(outputDirectory, "NA", "\t", collection);
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

}
