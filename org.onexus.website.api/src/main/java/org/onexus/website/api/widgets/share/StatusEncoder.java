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
package org.onexus.website.api.widgets.share;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class StatusEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusEncoder.class);
    private XStream xStream;

    public StatusEncoder(ClassLoader classLoader) {
        this.xStream = new XStream();
        this.xStream.setClassLoader(classLoader);
    }

    public String encodeStatus(Object status)
            throws UnsupportedEncodingException {
        return compress(xStream.toXML(status));
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeStatus(String strStatus)
            throws UnsupportedEncodingException {
        return (T) xStream.fromXML(decompress(strStatus));
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
                LOGGER.error("Decompressing '" + inputStr + "'", e);
            }
        }

        try {
            bos.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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
            LOGGER.error(e.getMessage());
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();

        // Encode Base64
        Base64 base64 = new Base64(-1, new byte[0], true);
        byte[] bytes = base64.encode(compressedData);
        return new String(bytes, "UTF-8");
    }

}
