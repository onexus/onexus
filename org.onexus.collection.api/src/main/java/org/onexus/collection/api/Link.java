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
package org.onexus.collection.api;

import org.onexus.resource.api.ORI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Link implements Serializable {

    public final static String FIELDS_SEPARATOR = "==";

    private ORI collection;
    private List<String> fields = new ArrayList<String>();

    public Link() {
        super();
    }

    public Link(ORI collection, List<String> fields) {
        super();
        this.collection = collection;
        this.fields = fields;
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fieldNames) {
        this.fields = fieldNames;
    }

    @Override
    public String toString() {
        return "Link [collection=" + collection + ", fields="
                + fields + "]";
    }

    public static String getToFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return (values.length == 2 ? values[1].trim() : values[0].trim());
    }

    public static String getFromFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return values[0].trim();
    }

}
