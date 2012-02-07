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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onexus.ui.website.widgets.WidgetStatus;

public class FiltersWidgetStatus extends WidgetStatus {

    private Set<String> activeFilters;

    private List<FilterConfig> userFilters = new ArrayList<FilterConfig>();

    public FiltersWidgetStatus() {
	super();
    }

    public FiltersWidgetStatus(String widgetId, String... activeFilters) {
	super(widgetId);
	this.activeFilters = new HashSet<String>(Arrays.asList(activeFilters));

    }

    public Set<String> getActiveFilters() {
	return activeFilters;
    }

    public void setActiveFilters(Set<String> activeFilters) {
	this.activeFilters = activeFilters;
    }

    public List<FilterConfig> getUserFilters() {
	return userFilters;
    }

    public void setUserFilters(List<FilterConfig> userFilters) {
	this.userFilters = userFilters;
    }

    public void updateFilter(FilterConfig filter) {
	if (filter.getActive()) {
	    activeFilters.add(filter.getId());
	} else {
	    if (activeFilters.contains(filter.getId())) {
		activeFilters.remove(filter.getId());
	    }
	}
    }

}
