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

import org.onexus.core.ICollectionManager;
import org.onexus.core.IQueryParser;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.utils.visible.FixedEntitiesVisiblePredicate;
import org.onexus.ui.website.widgets.WidgetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class FiltersWidgetStatus extends WidgetStatus<FiltersWidgetConfig> {

    private static final Logger log = LoggerFactory.getLogger(FiltersWidgetStatus.class);

    @Inject
    private IQueryParser queryParser;

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

    @Override
    public void onQueryBuild(Query query) {

        List<Filter> rules = new ArrayList<Filter>();

        for (FilterConfig filter : filters) {
            if (filter.getActive()) {
                createFilter(rules, filter, query);
            }
        }

        if (!rules.isEmpty()) {
            boolean union = (getConfig().getUnion() != null && getConfig().getUnion().booleanValue());
            if (!union) {
                QueryUtils.and(query, QueryUtils.joinAnd(rules));
            } else {
                QueryUtils.and(query, QueryUtils.joinOr(rules));
            }
        }


    }

    private void createFilter(List<Filter> filters, FilterConfig filter, Query query) {

        String oqlDefine = filter.getDefine();
        String oqlWhere = filter.getWhere();

        if (oqlDefine != null && oqlWhere != null) {
            Map<String, String> define = getQueryParser().parseDefine(oqlDefine);
            Filter where = getQueryParser().parseWhere(oqlWhere);

            if (define == null || where == null) {
                log.error("Malformed filter definition\n DEFINE: " + filter.getDefine() + "\n WHERE: " + filter.getWhere() + "\n");

            } else {
                for (Map.Entry<String, String> entry : define.entrySet()) {
                    query.addDefine(entry.getKey(), entry.getValue());
                }

                filters.add(where);
            }
        }
    }

    private IQueryParser getQueryParser() {

        if (queryParser == null) {
            OnexusWebApplication app = OnexusWebApplication.get();
            if (app != null) {
                app.getInjector().inject(this);
            }
        }

        return queryParser;
    }

}
