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
package org.onexus.collection.loader.tsv.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TsvEntitySet extends TsvEntity implements IEntitySet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TsvEntitySet.class);

    private BufferedReader bufferedReader;
    private Iterator<InputStream> isIterator;

    public TsvEntitySet(IDataStreams dataStreams, Collection collection) {
        super(collection, "", -1);

        Loader loader = collection.getLoader();

        NULL_CHAR = loader.getParameter("NULL_VALUE");
        if (NULL_CHAR == null) {
            NULL_CHAR = "-";
        }

        SEPARATOR = loader.getParameter("SEPARATOR");
        if (SEPARATOR == null) {
            SEPARATOR = "\t";
        }

        String field_map = loader.getParameter("FIELD_MAP");
        if (field_map != null) {
            Properties fieldMap = new Properties();
            try {
                fieldMap.load(new StringReader(field_map));
                setFieldIdToHeader(fieldMap);
            } catch (IOException e) {
                LOGGER.warn("Malformed loader FIELD_MAP at " + collection.getORI(), e);
            }
        }

        isIterator = dataStreams.iterator();

        if (isIterator.hasNext()) {
            nextInputStream();
        }

    }

    private void nextInputStream() {

        try {
            // Close previous file channel
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }

            InputStream is = isIterator.next();
            this.bufferedReader = new BufferedReader(new InputStreamReader(is));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String line = bufferedReader.readLine();

            // Extract static fields
            Map<String, String> staticFields = new HashMap<String, String>();
            while (line != null && (line.isEmpty() || line.charAt(0) == '#')) {

                if (line.length() > 2 && line.charAt(1) == '#') {
                    addStaticField(staticFields, line.substring(2));
                }

                line = bufferedReader.readLine();
            }
            setStaticFieldsValues(staticFields);

            // Extract header
            setHeaders(new HashMap<String, Integer>());

            int i = 0;
            String header = parseField(line, i);
            while (header != null) {
                getHeaders().put(header, i);
                i++;
                header = parseField(line, i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void addStaticField(Map<String, String> staticFields, String line) {

        int equal = line.indexOf('=');
        if (equal == -1) {
            return;
        }

        String key = line.substring(0, equal).trim();
        String value = line.substring(equal + 1).trim();

        staticFields.put(key, value);
    }

    @Override
    public boolean next() {

        // Read first row of data
        if (bufferedReader == null) {
            return false;
        }

        String currentLine = nextLine();

        if (currentLine == null) {
            return false;
        }

        setLine(currentLine);
        return true;

    }

    private String nextLine() {

        try {

            if (bufferedReader == null) {
                return null;
            }

            String line = nextNonCommentLine();

            if (line == null) {
                if (isIterator.hasNext()) {
                    nextInputStream();
                    return nextLine();
                } else {
                    bufferedReader.close();
                }
            } else {
                return line;
            }

        } catch (IOException e) {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    LOGGER.error("Error closing the reader", e1);
                }
        }

        return null;

    }

    private String nextNonCommentLine() throws IOException {

        String line = bufferedReader.readLine();
        while (line != null && (line.isEmpty() || line.charAt(0) == '#')) {
            line = bufferedReader.readLine();
        }

        return line;
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEntity detachedEntity() {
        return new TsvEntity(getCollection(), getLine(), getPosition());
    }

    @Override
    public Iterator<IEntity> iterator() {
        return new EntityIterator(this);
    }

    @Override
    public void close() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e1) {
                LOGGER.error("Error closing the reader", e1);
            }
        }
    }

}
