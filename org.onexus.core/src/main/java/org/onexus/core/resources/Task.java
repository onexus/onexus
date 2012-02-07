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

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private String toolURI;
    private List<ParameterValue> parameters;

    public Task() {
        super();
    }

    public Task(String toolURI, List<ParameterValue> parameters) {
        super();
        this.toolURI = toolURI;
        this.parameters = parameters;
    }


    public String getToolURI() {
        return toolURI;
    }

    public void setToolURI(String toolURI) {
        this.toolURI = toolURI;
    }

    public List<ParameterValue> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterValue> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return getParameter(String.class, key);
    }

    @SuppressWarnings("unchecked")
    private <T> T getParameter(Class<T> valueClass, String key) {
        for (ParameterValue value : parameters) {
            if (value.getKey().equals(key)) {
                return (T) value.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Task [toolURI=" + toolURI + ", parameters=" + parameters + "]";
    }

}
