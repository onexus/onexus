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
package org.onexus.ui.website.widgets.export;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.time.Time;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.core.IResourceManager;
import org.onexus.core.query.Query;
import org.onexus.core.resources.Collection;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig.ExportColumn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ExportResource extends AbstractResource {

    public final static String STATUS = "query";
    public final static String FILENAME = "filename";
    public static final String COLUMNS = "columns";
    public final static String FORMAT_TSV = "tsv";

    private final static XStream xstream = new XStream();

    protected String getFieldSeparator() {
        return "\t";
    }

    protected String getRowSeparator() {
        return "\n";
    }

    protected String getNullValue() {
        return "";
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {

        ResourceResponse response = new ResourceResponse();

        response.setContentType("text/csv");
        response.setLastModified(Time.now());

        if (response.dataNeedsToBeWritten(attributes)) {

            String filename = attributes.getParameters().get(FILENAME)
                    .toString();

            response.setFileName(filename);
            response.setContentDisposition(ContentDisposition.ATTACHMENT);

            response.setWriteCallback(new WriteCallback() {
                @Override
                public void writeData(final Attributes attributes) {

                    Query query = null;
                    String strQuery = attributes.getParameters().get(STATUS)
                            .toString();

                    if (strQuery != null) {
                        try {
                            query = decodeQuery(strQuery);
                        } catch (UnsupportedEncodingException e) {
                            throw new WicketRuntimeException(
                                    "Unable to decode the URL parameter 'query'",
                                    e);
                        }
                    }

                    String strColumns[] = attributes.getParameters()
                            .get(COLUMNS).toString().split(":::");
                    List<ExportColumn> columns = new ArrayList<ExportColumn>();
                    for (String column : strColumns) {
                        columns.add(new ExportColumn(column));
                    }

                    writeTSV(attributes.getResponse(), query, columns);
                }
            });

        }

        return response;
    }

    private void writeTSV(Response pw, Query query, List<ExportColumn> columns) {

        ICollectionManager em = OnexusWebSession.get()
                .getCollectionManager();
        IEntityTable data = em.load(query);

        // Print header
        Iterator<ExportColumn> itc = columns.iterator();
        while (itc.hasNext()) {
            ExportColumn col = itc.next();
            Collection c = getCollection(query.getMainNamespace(),
                    col.getCollectionURI());
            Iterator<String> itf = Arrays.asList(col.getFieldNames())
                    .iterator();
            while (itf.hasNext()) {
                String field = itf.next().trim();
                pw.write(c.getName() + "." + field);
                if (itf.hasNext()) {
                    pw.write(getFieldSeparator());
                }
            }

            if (itc.hasNext()) {
                pw.write(getFieldSeparator());
            }
        }
        pw.write(getRowSeparator());

        // Print values
        while (data.next()) {

            itc = columns.iterator();
            while (itc.hasNext()) {
                ExportColumn col = itc.next();
                Iterator<String> itf = Arrays.asList(col.getFieldNames())
                        .iterator();

                Collection c = getCollection(query.getMainNamespace(),
                        col.getCollectionURI());
                IEntity e = data.getEntity(c.getURI());

                while (itf.hasNext()) {
                    String field = itf.next().trim();
                    Object value = e.get(field);
                    pw.write((value == null ? getNullValue() : value.toString()));
                    if (itf.hasNext()) {
                        pw.write(getFieldSeparator());
                    }
                }

                if (itc.hasNext()) {
                    pw.write(getFieldSeparator());
                }
            }

            pw.write(getRowSeparator());
        }

    }

    private Collection getCollection(String releaseURI, String collectionURI) {
        IResourceManager rm = OnexusWebSession.get().getResourceManager();
        return rm.load(Collection.class,
                ResourceTools.getAbsoluteURI(releaseURI, collectionURI));
    }

    public static String encodeQuery(Query query)
            throws UnsupportedEncodingException {
        return compress(xstream.toXML(query));
    }

    public static Query decodeQuery(String strQuery)
            throws UnsupportedEncodingException {
        return (Query) xstream.fromXML(decompress(strQuery));
    }

    private static String decompress(String inputStr)
            throws UnsupportedEncodingException {

        // Base64 decode
        Base64 base64 = new Base64(-1, new byte[0], true);
        byte[] bytes = base64.decode(inputStr.getBytes("UTF-8"));

        // Inflater
        Inflater decompressor = new Inflater();
        decompressor.setInput(bytes);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();

        return new String(decompressedData, "UTF-8");
    }

    private static String compress(String inputStr)
            throws UnsupportedEncodingException {
        byte[] input = inputStr.getBytes("UTF-8");

        // Compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();

        // Encode Base64
        Base64 base64 = new Base64(-1, new byte[0], true);
        byte[] bytes = base64.encode(compressedData);
        return new String(bytes, "UTF-8");
    }

}
