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
package org.onexus.website.api.widgets.tableviewer.decorators.tooltip;

import org.onexus.resource.api.ParameterKey;


public enum TooltipDecoratorParameters implements ParameterKey {

    LENGTH("length", "Number of characters to show", null, false),
    ICON("icon", "CSS class of the tooltip icon", "icon-chevron-right", true),
    PLACEMENT("placement", "Tooltip placement. (top, right, bottom, left)", "bottom", true),
    OPACITY("opacity", "Icon opacity", "0.2", true);

    private final String key;
    private final String description;
    private final String defaultValue;
    private final boolean optional;

    private TooltipDecoratorParameters(String key, String description, String defaultValue, boolean optional) {
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


