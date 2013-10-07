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
package org.onexus.resource.api.utils;

import org.onexus.resource.api.Property;

import java.util.List;

public class AbstractMetadata implements IMetadata {

    private String label;
    private String title;
    private String description;
    private List<Property> properties;

    public AbstractMetadata() {
        super();
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

}
