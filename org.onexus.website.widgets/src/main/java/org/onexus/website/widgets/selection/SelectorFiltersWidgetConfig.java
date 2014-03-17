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
package org.onexus.website.widgets.selection;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.resource.api.annotations.ResourceRegister;
import org.onexus.website.api.FilterConfig;
import org.onexus.website.api.widgets.WidgetConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ResourceAlias("widget-selector-filters")
@ResourceRegister({FilterConfig.class})
public class SelectorFiltersWidgetConfig extends WidgetConfig {

    @Valid
    private SelectorFiltersWidgetStatus defaultStatus;

    private String title;

    @NotNull @Valid
    @ResourceImplicitList("filter")
    private List<FilterConfig> filters;

    public SelectorFiltersWidgetConfig() {
        super();
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

    @Override
    public SelectorFiltersWidgetStatus createEmptyStatus() {
        return new SelectorFiltersWidgetStatus(getId());
    }

    public SelectorFiltersWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(SelectorFiltersWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
