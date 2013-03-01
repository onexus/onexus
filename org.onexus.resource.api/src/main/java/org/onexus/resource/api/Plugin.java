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
package org.onexus.resource.api;

import org.onexus.resource.api.utils.AbstractMetadata;

import java.util.ArrayList;
import java.util.List;

public class Plugin extends AbstractMetadata {

    private String id;

    private String location;

    private List<Parameter> parameters;

    public Plugin() {
        super();
    }

    public Plugin(String id, String location) {
        this.id = id;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParameter(String key) {
        if (parameters != null) {
            for (Parameter value : parameters) {
                if (value.getKey().equals(key)) {
                    return value.getValue();
                }
            }
        }
        return null;
    }

    public List<String> getParameterList(String key) {
        List<String> result = new ArrayList<String>();
        for (Parameter value : parameters) {
            if (value.getKey().equals(key)) {
                result.add(value.getValue());
            }
        }
        return result;
    }


    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
