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
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TsvEntity implements IEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(TsvEntity.class);

    private long position;
    private Collection collection;

    private String line;
    private Map<String, Integer> headers;
    private Properties fieldIdToHeader;
    private Map<String, Field> fields;
    private Map<String, String> staticFieldsValues;

    protected String NULL_CHAR = "-";
    protected String SEPARATOR = "\t";

    public TsvEntity(Collection collection, String line, long position) {
        this.collection = collection;
        this.position = position;
        this.line = line;

        this.fields = new HashMap<String, Field>();
        for (Field field : collection.getFields()) {
            this.fields.put(field.getId(), field);
        }
    }

    @Override
    public String getId() {
        return Long.toHexString(position);
    }

    protected long getPosition() {
        return position;
    }

    protected void setPosition(long position) {
        this.position = position;
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public Object get(String fieldId) {

        if (!fields.containsKey(fieldId)) {
            return null;
        }

        Field field = fields.get(fieldId);
        String value = null;
        String header = getHeader(field.getId());
        if (headers.containsKey(header)) {
            Integer position = headers.get(header);
            value = parseField(line, position);
        } else {
            if (staticFieldsValues.containsKey(header)) {
                value = staticFieldsValues.get(header);
            }
        }

        if (value == null) {
            return null;
        } else {

            // Remove "
            value = value.replace('"', ' ');

            // Trim blank spaces
            value = value.trim();

        }

        try {

            Class<?> fieldClass = field.getType();

            // TODO use adapter factory as in SQL manager
            if (fieldClass.equals(Boolean.class)) {
                return Boolean.valueOf(value.trim().equalsIgnoreCase("1"));
            }

            // For number types return null if the value is empty
            if (Number.class.isAssignableFrom(fieldClass) && value.equals("")) {
                return null;
            }

            Constructor<?> constructor = fieldClass.getConstructor(String.class);

            return constructor.newInstance(value);
        } catch (Exception e) {
            LOGGER.warn("The value '" + value
                    + "' for the field '" + fieldId
                    + "' at collection '" + collection
                    + "' is malformed on line '" + line + "'. Loading as NULL value.");

            return null;
        }

    }

    @Override
    public void put(String fieldURI, Object value) {
        throw new UnsupportedOperationException("Read-only TsvEntity");
    }

    public Map<String, Integer> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Integer> headers) {
        this.headers = headers;
    }

    public Map<String, String> getStaticFieldsValues() {
        return staticFieldsValues;
    }

    public void setStaticFieldsValues(Map<String, String> staticFieldsValues) {
        this.staticFieldsValues = staticFieldsValues;
    }

    public Properties getFieldIdToHeader() {
        return fieldIdToHeader;
    }

    public void setFieldIdToHeader(Properties fieldIdToHeader) {
        this.fieldIdToHeader = fieldIdToHeader;
    }

    protected String parseField(String str, int num) {

        int start = -1;
        for (int i = 0; i < num; i++) {
            start = str.indexOf(SEPARATOR, start + 1);
            if (start == -1) {
                return null;
            }
        }

        int end = str.indexOf(SEPARATOR, start + 1);
        if (end == -1) {
            end = str.length();
        }

        String result = str.substring(start + 1, end);

        if (result != null && result.equalsIgnoreCase(NULL_CHAR)) {
            return null;
        }

        return result.replace('"', ' ').trim();

    }

    private String getHeader(String fieldId) {

        if (fieldIdToHeader != null && fieldIdToHeader.containsKey(fieldId)) {
            return fieldIdToHeader.getProperty(fieldId);
        }

        return fieldId;
    }

    @Override
    public String toString() {
        return "{" +
                "collection=" + collection.getORI() +
                ", values='" + line + '\'' +
                '}';
    }
}
