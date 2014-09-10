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

import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.validations.ValidCollection;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceRegister;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A <code>Collection</code> is a resource that represents a collection of structured data.
 */
@ValidCollection
@ResourceAlias("collection")
@ResourceRegister({Link.class, Field.class, OrderBy.class})
public class Collection extends Resource {

    @NotNull
    @Valid
    private List<Field> fields;

    @Valid
    private List<Link> links;

    public Collection() {
        super();
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Field getField(String fieldId) {
        if (fieldId != null) {
            for (Field field : fields) {
                if (field.getId().equals(fieldId)) {
                    return field;
                }
            }
        }

        return null;
    }

}
