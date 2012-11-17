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
package org.onexus.website.api.widgets.tableviewer.decorators.link;

import org.onexus.resource.api.ParameterKey;


public enum LinkDecoratorParameters implements ParameterKey {

    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", false),
    TARGET("target", "The link target", true),
    SEPARATOR("separator", "The character separator of a list.", true),
    LENGTH("length", "Maximum number of characters", true),
    ICON("icon", "Use an icon to link", true),
    ICON_TITLE("icon-title", "Title to use as icon tooltip", true);

    private final String key;
    private final String description;
    private final boolean optional;

    private LinkDecoratorParameters(String key, String description, boolean optional) {
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


