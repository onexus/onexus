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
package org.onexus.website.api;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widgets.WidgetStatus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class WebsiteStatus extends WidgetStatus<WebsiteConfig> implements Serializable {

    private String currentPage;

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public List<WidgetStatus> getActiveChildren(ORI parentOri) {
        return Arrays.asList(getChild(currentPage));
    }

    public void onQueryBuild(Query query) {
        query.setOn(getConfig().getORI());

        super.onQueryBuild(query);
    }

    public static MetaDataKey<Query> queryKey = new MetaDataKey<Query>() {
    };

    public Query buildQuery(ORI resourceUri) {

        Query query = RequestCycle.get().getMetaData(queryKey);

        if (query != null) {
            return query;
        } else {
            query = new Query();
        }

        query.setOn(resourceUri);

        beforeQueryBuild(query);
        onQueryBuild(query);
        afterQueryBuild(query);

        RequestCycle.get().setMetaData(queryKey, query);

        return query;
    }

    public void encodeParameters(PageParameters parameters, String prefix, boolean global) {

        if (getChildren() != null) {
            parameters.add(Website.PARAMETER_CURRENT_PAGE, currentPage);
            WidgetStatus status = getChild(currentPage);
            status.encodeParameters(parameters, "p", global);
        }

    }

    public void decodeParameters(PageParameters parameters, String prefix) {

        if (getChildren() != null) {

            StringValue c = parameters.get(Website.PARAMETER_CURRENT_PAGE);
            if (!c.isEmpty()) {
                currentPage = c.toString();
            }

            WidgetStatus status = getChild(currentPage);
            if (status != null) {
                status.decodeParameters(parameters, "p");
            }
        }

    }

}
