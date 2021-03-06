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
package org.onexus.website.widget.tableviewer.decorators.link;

import org.onexus.resource.api.ParameterKey;


public enum LinkDecoratorParameters implements ParameterKey {

    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", null, false),
    TARGET("target", "The link target", null, true),
    SEPARATOR("separator", "The character separator of a list.", null, true),
    LENGTH("length", "Maximum number of characters", null, true),
    ICON("icon", "Use an icon to link", null, true),
    ICON_TITLE("icon-title", "Title to use as icon tooltip", null, true);

    private final String key;
    private final String description;
    private final String defaultValue;
    private final boolean optional;

    private LinkDecoratorParameters(String key, String description, String defaultValue, boolean optional) {
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
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }


}


