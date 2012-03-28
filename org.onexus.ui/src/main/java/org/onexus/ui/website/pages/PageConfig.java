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
package org.onexus.ui.website.pages;

import org.onexus.ui.website.widgets.WidgetConfig;

import java.io.Serializable;
import java.util.List;

public abstract class PageConfig implements Serializable {

    private String id;
    private String name;

    public PageConfig() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WidgetConfig getWidgetConfig(String id) {

        if (id != null) {
            for (WidgetConfig config : getWidgetConfigs()) {
                if (id.equals(config.getId())) {
                    return config;
                }
            }
        }
        return null;
    }

    public abstract List<WidgetConfig> getWidgetConfigs();

    public abstract PageStatus createEmptyStatus();

    public abstract PageStatus getDefaultStatus();


}
