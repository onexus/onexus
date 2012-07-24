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

import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.Logger;
import org.onexus.data.api.Task;
import org.onexus.data.api.utils.SingleDataStreams;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class BiomartCallable implements Callable<IDataStreams> {

    private Task task;
    private Logger logger;
    private BiomartRequest request;

    public BiomartCallable(Task task, BiomartRequest request) {
        this.request = request;

        String collectionURI = request.getData().getURI();
        this.task = task;
        this.logger = task.getLogger();

        logger.info("Preparing BIOMART collection '" + collectionURI + "'");
    }

    @Override
    public IDataStreams call() throws Exception {

        this.logger.info("Downloading and parsing the collection");

        // Open input stream
        try {
            URL martService = new URL(request.getMartService());

            logger.info("Connecting to '" + request.getMartService() + "'");

            HttpURLConnection conn = (HttpURLConnection) martService.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(request.getQuery().getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(request.getQuery());
            wr.flush();
            wr.close();

            // Get Response
            InputStream is = conn.getInputStream();
            task.setDone(true);

            return new SingleDataStreams(task, is);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
