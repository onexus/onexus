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
package org.onexus.loader.tsv.internal;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.onexus.resource.api.IDataManager;
import org.onexus.resource.api.IEntity;
import org.onexus.resource.api.IEntitySet;
import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.utils.EntityIterator;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.loader.tsv.internal.tools.BufferedFileChannel;
import org.onexus.loader.tsv.internal.tools.Token;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class FileEntitySet extends FileEntity implements IEntitySet {

    private ArchiveStreamFactory archiveFactory = new ArchiveStreamFactory();
    private IDataManager dataManager;
    private long currentPosition;
    private String currentLine;

    private Deque<URL> urls;
    private BufferedFileChannel fc;
    private BufferedReader fr;
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


    public FileEntitySet(IDataManager dataManager, Collection collection) {
        super(collection, "", -1);

        this.dataManager = dataManager;
        this.urls = new ArrayDeque<URL>();

        String sourceURI = ResourceUtils.getAbsoluteURI(
                ResourceUtils.getParentURI(collection.getURI()),
                collection.getLoader().getParameter("SOURCE_URI")
        );

        initializeURLs(sourceURI, ResourceUtils.getProperties(collection.getURI()));

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

    private void initializeURLs(String strUrl, Map<String, String> properties) {

        strUrl = replaceProperties(strUrl, properties);

        // Check if the URL is a directory
        urls.addAll(dataManager.retrieve(strUrl));

    }

    private String replaceProperties(String strUrl, Map<String, String> properties) {

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                strUrl = strUrl.replaceAll(Pattern.quote("${" + entry.getKey() + "}"), entry.getValue());
            }
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

            String fileName = url.getFile();

            if ("file".equals(url.getProtocol()) && !fileName.endsWith(".tar.gz")) {
                this.file = new File(url.toURI());
                this.fc = new BufferedFileChannel(file);
                this.fr = null;
            } else {
                this.file = null;
                this.fc = null;

                InputStream in = url.openStream();

                try {

                    CompressorInputStream cin = null;
                    if (fileName.endsWith(".tar.gz")) {
                        cin = new GzipCompressorInputStream(in);
                    }

                    if (cin != null) {
                        TarArchiveInputStream tin = new TarArchiveInputStream(cin);
                        tin.getNextEntry();
                        this.fr = new BufferedReader(new InputStreamReader(tin));
                    }

                } catch (IOException e) {
                }

                if (this.fr == null) {
                    this.fr = new BufferedReader(new InputStreamReader(in));
                }

            }

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
            String firstLine;
            if (this.fc == null) {
                firstLine = this.fr.readLine();
            } else {
                firstLine = this.fc.readLine(Token.UTF8);
            }

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

            if (fc == null && fr == null) {
                return false;
            }

            if (fc != null) {
                currentPosition = fc.position();
            } else {
                currentPosition = -1;
            }

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

            if (fc == null && fr == null) {
                return null;
            }

            String line;

            if (fc != null) {
                line = fc.readLine(Token.UTF8);
            } else {
                line = fr.readLine();
            }

            if (line == null) {
                if (!urls.isEmpty()) {
                    nextFileChannel();
                    return nextLine();
                } else {
                    if (fc != null) {
                        fc.close();
                    } else {
                        fr.close();
                    }
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

            if (fr != null)
                try {
                    fr.close();
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

        if (fr != null) {
            try {
                fr.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
