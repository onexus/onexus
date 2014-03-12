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
package org.onexus.website.api.widgets.selection.custom;

import org.onexus.resource.api.ORI;
import org.onexus.resource.api.annotations.ResourceAlias;

import java.io.Serializable;

@ResourceAlias("custom-filter")
public class CustomFilter implements Serializable {

    private String title;
    private ORI collection;

    private String type;

    private String field;

    public CustomFilter() {
        super();
    }

    public CustomFilter(String title, ORI collectionURI, String field) {
        super();
        this.title = title;
        this.collection = collectionURI;
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collectionURI) {
        this.collection = collectionURI;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
