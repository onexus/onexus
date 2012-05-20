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

import org.onexus.core.IEntity;
import org.onexus.core.IEntitySet;
import org.onexus.core.TaskStatus;
import org.onexus.core.utils.EntityIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class BiomartEntitySet extends TSVEntity implements IEntitySet {

    private final static Logger LOGGER = LoggerFactory.getLogger(BiomartEntitySet.class);


    private TaskStatus taskStatus;
    private HttpURLConnection conn;
    private BufferedReader reader;

    private long lastLogTime;
    private long currentPosition = 0;

    public BiomartEntitySet(TaskStatus taskStatus, BiomartRequest request) {
        super(request.getCollection(), "");

        this.taskStatus = taskStatus;
        String query = request.getQuery();

        try {
            URL martService = new URL(request.getMartService());

            taskStatus.addLog("Connecting to '" + request.getMartService() + "'");
            LOGGER.debug("Connecting to '{}'", request.getMartService());

            this.lastLogTime = System.currentTimeMillis();

            conn = (HttpURLConnection) martService.openConnection();


            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(query.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            // Get Response
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            taskStatus.setDone(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean next() {

        try {
            setLine(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (getLine() == null) {
            taskStatus.addLog("Reading done.");
            LOGGER.debug("Reading done.");
            return false;
        }

        if ((System.currentTimeMillis() - this.lastLogTime) > 4000) {
            taskStatus.addLog(String.format("Reading line %d", currentPosition));
            LOGGER.debug("Reading line {}", currentPosition);
            this.lastLogTime = System.currentTimeMillis();
        }

        currentPosition++;

        return true;
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

        try {
            if (reader != null) {
                reader.close();
            }

            if (conn != null) {
                conn.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IEntity detachedEntity() {
        return new TSVEntity(getCollection(), getLine());
    }

    @Override
    public Iterator<IEntity> iterator() {
        return new EntityIterator(this);
    }

}
