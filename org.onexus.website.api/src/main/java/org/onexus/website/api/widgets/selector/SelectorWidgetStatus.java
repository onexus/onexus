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
package org.onexus.website.api.widgets.selector;

import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.widgets.WidgetStatus;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class SelectorWidgetStatus extends WidgetStatus<SelectorWidgetConfig> {

    private String selection;

    @PaxWicketBean(name="queryParser")
    private IQueryParser queryParser;


    public SelectorWidgetStatus() {
        super();
    }

    public SelectorWidgetStatus(String widgetId) {
        super(widgetId);
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    @Override
    public void onQueryBuild(Query query) {

        if (selection != null) {
            SelectorWidgetConfig config = getConfig();
            String collectionAlias = QueryUtils.newCollectionAlias(query, config.getCollection());
            QueryUtils.and(query, new Equal(collectionAlias, config.getField(), selection));

            String oqlWhere = getConfig().getWhere();

            if (oqlWhere != null && !oqlWhere.isEmpty()) {
                Filter where = getQueryParser().parseWhere(collectionAlias + "." + getConfig().getWhere().trim());
                QueryUtils.and(query, where);
            }
        }

    }

    private IQueryParser getQueryParser() {
        if (queryParser == null) {
            WebsiteApplication.inject(this);
        }

        return queryParser;
    }
}
