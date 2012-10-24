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

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.ui.website.pages.browser.IFilter;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SelectorFiltersWidgetStatus extends WidgetStatus<FiltersWidgetConfig> {

    private static final Logger log = LoggerFactory.getLogger(SelectorFiltersWidgetStatus.class);

    private List<FilterConfig> selectedFilters;

    public SelectorFiltersWidgetStatus() {
        super();
    }

    public SelectorFiltersWidgetStatus(String widgetId) {
        super(widgetId);
    }

    @Override
    public String getButton() {
        if (selectedFilters != null && !selectedFilters.isEmpty()) {
            return super.getButton() + " <span class=\"badge badge-warning\">" + selectedFilters.size() + "</span>";
        }

        return super.getButton();
    }

    public List<FilterConfig> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(List<FilterConfig> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    @Override
    public void onQueryBuild(Query query) {

        if (selectedFilters != null && !selectedFilters.isEmpty()) {
            List<Filter> orFilters = new ArrayList<Filter>();
            for (FilterConfig selectedFilter : selectedFilters) {
                BrowserFilter browserFilter = new BrowserFilter(selectedFilter);
                orFilters.add(browserFilter.buildFilter(query));
            }
            Filter allFilters = QueryUtils.joinOr(orFilters);
            QueryUtils.and(query, allFilters);
        }

    }
}
