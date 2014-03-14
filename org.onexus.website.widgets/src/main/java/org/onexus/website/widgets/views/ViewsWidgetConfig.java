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
package org.onexus.website.widgets.views;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.widgets.WidgetConfig;

@ResourceAlias("widget-views")
public class ViewsWidgetConfig extends WidgetConfig {

    private ViewsWidgetStatus defaultStatus;

    public ViewsWidgetConfig() {
        super();
    }

    public ViewsWidgetConfig(String id) {
        super(id);
    }

    @Override
    public ViewsWidgetStatus createEmptyStatus() {
        return new ViewsWidgetStatus(getId());
    }

    public ViewsWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(ViewsWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
