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
package org.onexus.loader.tsv;

import org.onexus.core.IEntity;
import org.onexus.core.IEntitySet;
import org.onexus.core.ISourceManager;
import org.onexus.core.resources.Collection;
import org.onexus.core.utils.EntityIterator;
import org.onexus.loader.tsv.tools.BufferedFileChannel;
import org.onexus.loader.tsv.tools.Token;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class FileEntitySet extends FileEntity implements IEntitySet {

    private ISourceManager sourceManager;
    private Map<String, String> properties;
    private long currentPosition;
    private String currentLine;

    private Deque<URL> urls;
    private BufferedFileChannel fc;
    private File file;

    public FileEntitySet(String fileURL, String nullChar, String separator, Collection collection) {
        super(collection, "", -1);

        this.urls = new ArrayDeque<URL>();

        try {
            URL url = new URL(fileURL);
            urls.add(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        NULL_CHAR = nullChar;
        SEPARATOR = separator;

        if (!urls.isEmpty()) {
            nextFileChannel();
        }

    }


    public FileEntitySet(ISourceManager sourceManager, Map<String, String> properties, Collection collection) {
        super(collection, "", -1);

        this.sourceManager = sourceManager;
        this.properties = properties;
        this.urls = new ArrayDeque<URL>();

        initializeURLs(collection.getLoader().getParameter("SOURCE_URI"));

        NULL_CHAR = collection.getLoader().getParameter("NULL_VALUE");
        SEPARATOR = collection.getLoader().getParameter("SEPARATOR");

        if (NULL_CHAR == null) {
            NULL_CHAR = "-";
        }

        if (SEPARATOR == null) {
            SEPARATOR = "\t";
        }

        if (!urls.isEmpty()) {
            nextFileChannel();
        }

    }

    private void initializeURLs(String strUrl) {

        strUrl = replaceProperties(strUrl);

        int parametersSeparator = strUrl.indexOf("?");

        String resourceURI = (parametersSeparator > 0 ? strUrl.substring(0, parametersSeparator) : strUrl);

        // Check if the URL is a directory
        urls.addAll(sourceManager.retrieve(resourceURI));

    }

    private String replaceProperties(String strUrl) {

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            strUrl = strUrl.replaceAll(Pattern.quote("${" + entry.getKey() + "}"), entry.getValue());
        }

        return strUrl;
    }

    private void nextFileChannel() {
        try {

            // Close previous file channel
            if (this.fc != null) {
                this.fc.close();
            }

            // Load next file
            URL url = urls.poll();
            this.file = new File(url.toURI());
            this.fc = new BufferedFileChannel(file);

        } catch (java.io.FileNotFoundException e) {

            this.file = null;
            this.fc = null;
            setHeaders(new HashMap<String, Integer>());

            return;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Read first line the headers
        try {
            String firstLine = this.fc.readLine(Token.UTF8);

            setHeaders(new HashMap<String, Integer>());

            int i = 0;
            String header = parseField(firstLine, i);
            while (header != null) {
                getHeaders().put(header, i);
                i++;
                header = parseField(firstLine, i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean next() {

        // Read first row of data
        try {

            if (fc == null) {
                return false;
            }

            currentPosition = fc.position();
            currentLine = nextLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (currentLine == null) {
            return false;
        }

        setLine(currentLine);
        setPosition(currentPosition);

        return true;

    }

    private String nextLine() {

        try {

            if (fc == null) {
                return null;
            }

            String line = fc.readLine(Token.UTF8);

            if (line == null) {
                if (!urls.isEmpty()) {
                    nextFileChannel();
                    return nextLine();
                } else {
                    fc.close();
                }
            } else {
                return line;
            }

        } catch (IOException e) {
            if (fc != null)
                try {
                    fc.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }

        return null;

    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEntity detachedEntity() {
        return new FileEntity(getCollection(), getLine(), getPosition());
    }

    @Override
    public Iterator<IEntity> iterator() {
        return new EntityIterator(this);
    }

    @Override
    public void close() {
        if (fc != null) {
            try {
                fc.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
