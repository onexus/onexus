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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.resource.api.utils.AbstractMetadata;

@XStreamAlias("field")
public class Field extends AbstractMetadata {

    private String id;
    private Class<?> type;

    @XStreamAlias("primary-key")
    private Boolean primaryKey;

    public Field() {
        super();
    }

    public Field(String id, String label, String title, Class<?> type) {
        this(id, label, title, type, null);
    }

    public Field(String id, String label, String title, Class<?> type, Boolean primaryKey) {
        super();
        this.id = id;
        this.type = type;
        this.primaryKey = primaryKey;
        setLabel(label);
        setTitle(title);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public Boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Field other = (Field) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", primaryKey=" + primaryKey +
                '}';
    }
}
