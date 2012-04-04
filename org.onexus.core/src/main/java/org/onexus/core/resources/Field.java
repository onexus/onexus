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

public class Field implements IMetadata {

    private String id;
    private String label;
    private String title;
    private String description;
    private Class<?> type;
    private Boolean primaryKey;
    private List<Property> properties;

    public Field() {
        super();
    }

    public Field(String id, String label, String title, Class<?> type) {
        super();
        this.id = id;
        this.label = label;
        this.title = title;
        this.type = type;
        this.primaryKey = null;
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

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public String getProperty(String key) {
        if (this.properties == null) {
            return null;
        }

        for (Property p : this.properties) {
            if (p.getKey().equals(key)) {
                return p.getValue();
            }
        }
        return null;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Field other = (Field) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Field [id=" + id + ", label=" + label + ", title="
                + title + ", description=" + description + ", type="
                + type + ", primaryKey=" + primaryKey + ", properties="
                + properties + "]";
    }

}
