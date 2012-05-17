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

import org.onexus.core.IQueryParser;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class SelectorFilterWidgetStatus extends WidgetStatus<SelectorFilterWidgetConfig> {

    private static final Logger log = LoggerFactory.getLogger(SelectorFilterWidgetStatus.class);

    @Inject
    private IQueryParser queryParser;

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

    @Override
    public void onQueryBuild(Query query) {


        String activeFilter = getActiveFilter();

        if (activeFilter != null) {

            for (FilterConfig filter : getConfig().getFilters()) {
                if (activeFilter.equals(filter.getId())) {

                    Map<String, String> define = queryParser.parseDefine(filter.getDefine());
                    Filter where = queryParser.parseWhere(filter.getWhere());

                    if (define == null || where == null) {
                        log.error("Malformed filter definition\n DEFINE: " + filter.getDefine() + "\n WHERE: " + filter.getWhere() + "\n");

                    } else {
                        for (Map.Entry<String, String> entry : define.entrySet()) {
                            query.addDefine(entry.getKey(), entry.getValue());
                        }

                        QueryUtils.and(query, where);
                    }


                }
            }

        }

    }


}
