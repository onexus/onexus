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
import org.onexus.data.api.Progress;
import org.onexus.data.api.utils.SingleDataStreams;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

public class BiomartCallable implements Callable<IDataStreams> {

    private Progress progress;
    private BiomartRequest request;

    public BiomartCallable(Progress progress, BiomartRequest request) {
        this.request = request;

        String collectionURI = request.getData().getURI();
        this.progress = progress;

        progress.info("Preparing BIOMART collection '" + collectionURI + "'");
    }

    @Override
    public IDataStreams call() throws Exception {

        // Open input stream
        try {
            URL martService = new URL(request.getMartService());


            progress.info("Connecting to '" + request.getMartService() + "'");

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
            progress.setDone(true);

            return new SingleDataStreams(progress, is);

        } catch (MalformedURLException e) {

            String errMsg = "Malformed mart service url '" + request.getMartService() + "'";
            progress.error(errMsg);
            progress.setCancelled(true);
            throw new RuntimeException(errMsg);

        } catch (IOException e) {

            String errMsg = "Unable to connect to '" + request.getMartService() + "'";
            progress.error(errMsg);
            progress.setCancelled(true);
            throw new RuntimeException(errMsg);

        } finally {
            progress.setDone(true);
        }

    }

}
