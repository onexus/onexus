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
package org.onexus.website.api.widgets.selection;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.selection.custom.CustomFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ResourceAlias("widget-filters")
public class FiltersWidgetConfig extends WidgetConfig {

    private FiltersWidgetStatus defaultStatus;
    private String title;

    @ResourceImplicitList("custom-filter")
    private List<CustomFilter> customFilters;

    @ResourceImplicitList("filter")
    private List<FilterConfig> filters;

    public FiltersWidgetConfig() {
        super();
    }

    public FiltersWidgetConfig(String id, FilterConfig... filters) {
        this(id, null, filters);
    }

    public FiltersWidgetConfig(String id, CustomFilter[] customFilters, FilterConfig... filters) {
        super(id);

        if (customFilters == null || customFilters.length == 0) {
            this.customFilters = new ArrayList<CustomFilter>(0);
        } else {
            this.customFilters = new ArrayList<CustomFilter>(
                    Arrays.asList(customFilters));
        }
        this.filters = new ArrayList<FilterConfig>(Arrays.asList(filters));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public void addFilter(FilterConfig filter) {
        this.filters.add(filter);
    }

    public List<CustomFilter> getCustomFilters() {
        return customFilters;
    }

    public void setCustomFilters(List<CustomFilter> customFilters) {
        this.customFilters = customFilters;
    }

    @Override
    public FiltersWidgetStatus createEmptyStatus() {
        return new FiltersWidgetStatus(getId());
    }

    public FiltersWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(FiltersWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
