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
package org.onexus.core.resources;

import java.util.List;

public class Collection extends Resource {

    private Loader loader;
    private List<Field> fields;
    private List<Link> links;

    public Collection() {
        super();
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Collection [getURI()=" + getURI() + ", loader=" + loader
                + ", fields=" + fields + ", links=" + links + "]";
    }

    public Field getField(String fieldName) {
        if (fieldName != null) {
            for (Field field : fields) {
                if (field.getId().equals(fieldName)) {
                    return field;
                }
            }
        }

        return null;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
