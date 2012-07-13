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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.SerializationUtils;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XStreamAlias("widget-filters")
public class FiltersWidgetConfig extends WidgetConfig {

    private FiltersWidgetStatus defaultStatus;
    private String title;
    private Boolean userFilters;
    private List<FieldSelection> fieldSelection;
    private List<FilterConfig> filters;

    public FiltersWidgetConfig() {
        super();
    }

    public FiltersWidgetConfig(String id, FilterConfig... filters) {
        this(id, null, filters);
    }

    public FiltersWidgetConfig(String id, FieldSelection[] userFields, FilterConfig... filters) {
        super(id);

        if (userFields == null || userFields.length == 0) {
            this.userFilters = false;
            this.fieldSelection = new ArrayList<FieldSelection>(0);
        } else {
            this.userFilters = true;
            this.fieldSelection = new ArrayList<FieldSelection>(
                    Arrays.asList(userFields));
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

    public Boolean getUserFilters() {
        return userFilters;
    }

    public void setUserFilters(Boolean userFilters) {
        this.userFilters = userFilters;
    }

    public List<FieldSelection> getFieldSelection() {
        return fieldSelection;
    }

    public void setFieldSelection(List<FieldSelection> fieldSelection) {
        this.fieldSelection = fieldSelection;
    }

    @Override
    public FiltersWidgetStatus createEmptyStatus() {

        FiltersWidgetStatus status = new FiltersWidgetStatus(getId());
        if (filters != null) {
            for (FilterConfig filter : filters) {
                 status.getFilters().add(SerializationUtils.clone(filter));
            }
        }

        return status;

    }

    public FiltersWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(FiltersWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
