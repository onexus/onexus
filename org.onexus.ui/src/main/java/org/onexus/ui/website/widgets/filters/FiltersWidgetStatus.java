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

    @Override
    public void onQueryBuild(Query query) {


        //TODO FixedEntitiesVisiblePredicate fixedPredicate = new FixedEntitiesVisiblePredicate(query.getOn(), status.getFixedEntities());

        List<Filter> rules = new ArrayList<Filter>();
        for (FilterConfig filter : userFilters) {
            // if (filter.getActive() && fixedPredicate.evaluate(filter)) {
            if (filter.getActive()) {

                String oqlDefine = filter.getDefine();
                String oqlWhere = filter.getWhere();

                if (oqlDefine != null && oqlWhere != null) {
                    Map<String, String> define = queryParser.parseDefine(oqlDefine);
                    Filter where = queryParser.parseWhere(oqlWhere);

                    if (define == null || where == null) {
                        log.error("Malformed filter definition\n DEFINE: " + filter.getDefine() + "\n WHERE: " + filter.getWhere() + "\n");

                    } else {
                        for (Map.Entry<String, String> entry : define.entrySet()) {
                            query.addDefine(entry.getKey(), entry.getValue());
                        }

                        rules.add(where);
                    }
                }


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

}
