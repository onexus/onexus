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
package org.onexus.website.api.widgets.filters;

import org.apache.commons.codec.binary.Base64;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.browser.IFilter;
import org.onexus.website.api.utils.visible.VisibleRule;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class BrowserFilter implements IFilter {

    private static final Logger log = LoggerFactory.getLogger(BrowserFilter.class);
    private FilterConfig config;
    private boolean enable;
    private boolean deletable;

    @PaxWicketBean(name = "queryParser")
    private IQueryParser queryParser;

    @PaxWicketBean(name = "resourceSerializer")
    private IResourceSerializer resourceSerializer;

    public BrowserFilter() {
    }

    public BrowserFilter(FilterConfig config) {
        this.config = config;
        this.enable = true;
        this.deletable = true;
    }

    @Override
    public FilterConfig getFilterConfig() {
        return config;
    }

    @Override
    public ORI getFilteredCollection() {
        return config.getCollection();
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean isDeletable() {
        return deletable;
    }

    @Override
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public Filter buildFilter(Query query) {

        String oqlDefine = config.getDefine();
        String oqlWhere = config.getWhere();

        if (oqlDefine != null && oqlWhere != null) {
            Map<String, ORI> define = getQueryParser().parseDefine(oqlDefine);


            if (define == null) {
                log.error("Malformed filter definition\n DEFINE: " + config.getDefine() + "\n");
            } else {

                for (Map.Entry<String, ORI> entry : define.entrySet()) {
                    String collectionAlias = QueryUtils.newCollectionAlias(query, entry.getValue());
                    oqlWhere = oqlWhere.replaceAll(entry.getKey() + ".", collectionAlias + ".");
                }

                Filter where = getQueryParser().parseWhere(oqlWhere);

                if (where == null) {
                    log.error("Malformed filter WHERE: " + oqlWhere + "\n");
                } else {
                    return where;
                }
            }
        }

        return null;
    }

    @Override
    public String getLabel(Query query) {
        return config.getDefine();
    }

    @Override
    public String getTitle(Query query) {
        return config.getName();
    }

    @Override
    public boolean match(VisibleRule rule) {

        ORI visibleCollection = config.getVisibleCollection();
        if (visibleCollection == null) {
            visibleCollection = config.getCollection();
        }

        //TODO
        boolean validCollection = visibleCollection.getPath().endsWith(rule.getFilteredCollection().getPath());

        if (rule.getField() == null) {
            return validCollection;
        } else {
            return false;
        }
    }

    @Override
    public String toUrlParameter() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            getResourceSerializer().serialize(config, out);
            return compress(out.toString());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    @Override
    public void loadUrlPrameter(String parameter) {

        try {
            config = getResourceSerializer().unserialize(FilterConfig.class, new StringBufferInputStream(decompress(parameter)));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }

        deletable = config.isDeletable();
        enable = true;
    }

    @Override
    public String getVisible() {
        return config.getVisible();
    }

    private IQueryParser getQueryParser() {

        if (queryParser == null) {
            WebsiteApplication.inject(this);
        }

        return queryParser;
    }

    private IResourceSerializer getResourceSerializer() {

        if (resourceSerializer == null) {
            WebsiteApplication.inject(this);
        }

        return resourceSerializer;
    }

    public static String decompress(String inputStr) throws UnsupportedEncodingException {

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

    public static String compress(String inputStr) throws UnsupportedEncodingException {
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
