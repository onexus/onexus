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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TsvEntitySet extends TsvEntity implements IEntitySet {

    private String currentLine;
    private BufferedReader fr;
    private Iterator<InputStream> isIterator;

    public TsvEntitySet(IDataStreams dataStreams, Collection collection) {
        super(collection, "", -1);

        NULL_CHAR = collection.getLoader().getParameter("NULL_VALUE");
        SEPARATOR = collection.getLoader().getParameter("SEPARATOR");

        if (NULL_CHAR == null) {
            NULL_CHAR = "-";
        }

        if (SEPARATOR == null) {
            SEPARATOR = "\t";
        }

        isIterator = dataStreams.iterator();

        if (isIterator.hasNext()) {
            nextInputStream();
        }

    }

    private void nextInputStream() {

        try {
            // Close previous file channel
            if (this.fr != null) {
                this.fr.close();
            }

            InputStream is = isIterator.next();
            this.fr = new BufferedReader(new InputStreamReader(is));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String line = fr.readLine();

            // Extract static fields
            Map<String, String> staticFields = new HashMap<String, String>();
            while(line != null && (line.isEmpty() || line.charAt(0) == '#')) {

                if (line.length() > 2 && line.charAt(1) == '#') {
                    addStaticField(staticFields, line.substring(2));
                }

                line = fr.readLine();
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

        String key = line.substring(0,equal).trim();
        String value = line.substring(equal+1).trim();

        staticFields.put(key, value);
    }

    @Override
    public boolean next() {

        // Read first row of data
        if (fr == null) {
            return false;
        }

        currentLine = nextLine();

        if (currentLine == null) {
            return false;
        }

        setLine(currentLine);
        return true;

    }

    private String nextLine() {

        try {

            if (fr == null) {
                return null;
            }

            String line = nextNonCommentLine();

            if (line == null) {
                if (isIterator.hasNext()) {
                    nextInputStream();
                    return nextLine();
                } else {
                    fr.close();
                }
            } else {
                return line;
            }

        } catch (IOException e) {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }

        return null;

    }

    private String nextNonCommentLine() throws IOException {

        String line = fr.readLine();
        while(line != null && (line.isEmpty() || line.charAt(0) == '#')) {
            line = fr.readLine();
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
        if (fr != null) {
            try {
                fr.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
