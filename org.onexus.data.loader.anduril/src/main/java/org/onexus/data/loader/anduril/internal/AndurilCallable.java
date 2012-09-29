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

import fi.helsinki.ltdk.csbl.anduril.core.engine.DynamicError;
import fi.helsinki.ltdk.csbl.anduril.core.engine.NetworkEvaluator;
import fi.helsinki.ltdk.csbl.anduril.core.network.Repository;
import fi.helsinki.ltdk.csbl.anduril.core.network.componentInstance.OutputComponentInstance;
import org.onexus.data.api.Data;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Progress;
import org.onexus.data.api.utils.EmptyDataStreams;
import org.onexus.data.api.utils.UrlDataStreams;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AndurilCallable implements Callable<IDataStreams> {

    private Progress progress;
    private NetworkEvaluator evaluator;
    private String executionDir;

    public AndurilCallable(Progress progress, Repository repository, String baseExecutionDir, Data data) {
        super();

        String collectionURI = data.getURI();
        String collectionHash = Long.toHexString(collectionURI.hashCode());

        this.progress = progress;

        progress.info("Preparing ANDURIL execution of '" + collectionURI + "'");

        this.executionDir = baseExecutionDir + File.separator + collectionHash;
        final File executionDirFile = new File(executionDir);
        if (!executionDirFile.exists()) {
            executionDirFile.mkdirs();
        }

        evaluator = new NetworkEvaluator(executionDirFile);
        String andurilScript = data.getLoader().getParameter("SCRIPT");
        evaluator.setSource(andurilScript, repository, repository.getSymbolTable());
        evaluator.parse();


    }

    @Override
    public IDataStreams call() throws Exception {


        if (!evaluator.hasErrors()) {
            progress.info("Starting Anduril execution");
            evaluator.execute();
        }

        if (evaluator.hasErrors()) {
            for (DynamicError error : evaluator.getEngineErrors()) {
                progress.error(error.format());
            }
            progress.fail();
            return new EmptyDataStreams(progress);

        }

        progress.done();

        String outputDirectory = "file:" + executionDir + File.separator + OutputComponentInstance.OUTPUT_DIRECTORY;
        File outFolder = new File(outputDirectory);

        List<URL> urls = new ArrayList<URL>();
        for (File file : outFolder.listFiles()) {
            urls.add(file.toURI().toURL());
        }
        return new UrlDataStreams(progress, urls);
    }

}
