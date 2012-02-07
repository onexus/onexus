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
package org.onexus.ui.website.widgets.filters;

import org.onexus.ui.website.widgets.WidgetStatus;

public class SelectorFilterWidgetStatus extends WidgetStatus {

    private String activeFilter;

    public SelectorFilterWidgetStatus() {
        super();
    }

    public SelectorFilterWidgetStatus(String widgetId) {
        super(widgetId);
    }

    public SelectorFilterWidgetStatus(String widgetId, String activeFilter) {
        super(widgetId);
        this.activeFilter = activeFilter;
    }

    public String getActiveFilter() {
        return activeFilter;
    }

    public void setActiveFilter(String activeFilter) {
        this.activeFilter = activeFilter;
    }

}
