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
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ResourceAlias("link")
public class Link implements Serializable {

    private ORI collection;

    @ResourceImplicitList("field")
    private List<String> fields = new ArrayList<String>();

    public Link() {
        super();
    }

    public Link(ORI collection, String... fields) {
        super();
        this.collection = collection;
        this.fields = Arrays.asList(fields);
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

}
