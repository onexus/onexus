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
package org.onexus.website.api.widgets.tableviewer.decorators.scale;

import org.onexus.resource.api.ParameterKey;

public enum PValueDecoratorParameters implements ParameterKey {

    SHOW_VALUE("show-value", "Print the value inside the cell. Valid values: true, false", true),
    SIGNIFICANCE("significance", "p-value signinficance cutoff. Default '0.05'.", true),
    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", false),
    URL_TITLE("url-title", "The tooltip title to show over URL link button", false);

    private final String key;
    private final String description;
    private final boolean optional;

    private PValueDecoratorParameters(String key, String description, boolean optional) {
        this.key = key;
        this.description = description;
        this.optional = optional;
    }

    @Override
    public String getKey() {
        return key;
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
