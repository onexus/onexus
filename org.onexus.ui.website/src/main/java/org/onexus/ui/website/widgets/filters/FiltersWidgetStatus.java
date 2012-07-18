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
package org.onexus.ui.website.widgets.filters;

import org.onexus.ui.website.widgets.WidgetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class FiltersWidgetStatus extends WidgetStatus<FiltersWidgetConfig> {

    private static final Logger log = LoggerFactory.getLogger(FiltersWidgetStatus.class);

    private List<FilterConfig> filters = new ArrayList<FilterConfig>();

    public FiltersWidgetStatus() {
        super();
    }

    public FiltersWidgetStatus(String widgetId) {
        super(widgetId);
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

}
