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
package org.onexus.website.widgets.tableviewer.decorators.scale;

import org.onexus.resource.api.ParameterKey;

public enum HeatDecoratorParameters implements ParameterKey {

    MIN_VALUE("min-value", "Minimum scale value", null, true),
    MID_VALUE("mid-value", "Medium scale value", null, true),
    MAX_VALUE("max-value", "Maximum scale value", null, true),
    MIN_COLOR("min-color", "Minimum scale color", null, true),
    MID_COLOR("mid-color", "Medium scale color", null, true),
    MAX_COLOR("max-color", "Maximum scale color", null, true),
    SHOW_VALUE("show-value", "Print the value inside the cell. Valid values: true, false", null, true),
    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", null, true),
    URL_TITLE("url-title", "The tooltip title to show over URL link button", null, true);

    private final String key;
    private final String description;
    private final String defaultValue;
    private final boolean optional;

    private HeatDecoratorParameters(String key, String description, String defaultValue, boolean optional) {
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
        this.optional = optional;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
