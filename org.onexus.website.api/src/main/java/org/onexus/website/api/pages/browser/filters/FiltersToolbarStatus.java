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
package org.onexus.website.api.pages.browser.filters;

import org.onexus.website.api.widgets.WidgetStatus;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.selection.FiltersWidgetConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class FiltersToolbarStatus extends WidgetStatus<FiltersWidgetConfig> {

    @NotNull @Valid
    private List<FilterConfig> filters;

    public FiltersToolbarStatus() {
        super();
    }

    public FiltersToolbarStatus(String widgetId) {
        super(widgetId);
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }
}
