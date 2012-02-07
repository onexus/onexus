/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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

    private String name;
    private String shortName;
    private String title;
    private String description;
    private Class<?> dataType;
    private boolean primaryKey;
    private List<Property> properties;

    public Field() {
        super();
    }

    public Field(String name, String shortName, String title, Class<?> dataType) {
        super();
        this.name = name;
        this.shortName = shortName;
        this.title = title;
        this.dataType = dataType;
        this.primaryKey = false;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

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

    public Class<?> getDataType() {
        return dataType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Field [name=" + name + ", shortName=" + shortName + ", title="
                + title + ", description=" + description + ", dataType="
                + dataType + ", primaryKey=" + primaryKey + ", properties="
                + properties + "]";
    }

}
