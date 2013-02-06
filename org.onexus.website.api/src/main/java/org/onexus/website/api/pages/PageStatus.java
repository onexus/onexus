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
package org.onexus.website.api.pages;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.widgets.WidgetStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PageStatus<C extends PageConfig> implements Serializable {

    private String id;

    private transient C config;

    private List<WidgetStatus> widgetStatuses = new ArrayList<WidgetStatus>();


    public PageStatus() {
        super();
    }

    public PageStatus(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public C getConfig() {
        return config;
    }

    public void setConfig(C config) {
        this.config = config;
    }

    public WidgetStatus getWidgetStatus(String id) {

        if (id == null) {
            return null;
        }

        for (WidgetStatus status : getWidgetStatuses()) {
            if (id.equals(status.getId())) {
                return status;
            }
        }

        return null;
    }

    public void setWidgetStatus(WidgetStatus status) {
        WidgetStatus oldStatus = getWidgetStatus(status.getId());
        widgetStatuses.add(status);
        widgetStatuses.remove(oldStatus);
    }

    public List<WidgetStatus> getWidgetStatuses() {
        return widgetStatuses;
    }

    public List<WidgetStatus> getActiveWidgetStatuses(ORI parentOri) {
        return getWidgetStatuses();
    }

    public void setWidgetStatuses(List<WidgetStatus> widgetStatuses) {
        this.widgetStatuses = widgetStatuses;
    }

    public MetaDataKey<Query> QUERY = new MetaDataKey<Query>() {
    };

    public Query buildQuery(ORI resourceUri) {

        Query query = RequestCycle.get().getMetaData(QUERY);

        if (query != null) {
            return query;
        } else {
            query = new Query();
        }

        query.setOn(resourceUri);

        List<WidgetStatus> activeWidgets = getActiveWidgetStatuses(resourceUri);

        // Before cycle
        beforeQueryBuild(query);
        if (activeWidgets != null && !activeWidgets.isEmpty()) {
            for (WidgetStatus status : activeWidgets) {
                status.beforeQueryBuild(query);
            }
        }

        // On cycle
        onQueryBuild(query);
        if (activeWidgets != null && !activeWidgets.isEmpty()) {
            for (WidgetStatus status : activeWidgets) {
                status.onQueryBuild(query);
            }
        }

        // After cycle
        afterQueryBuild(query);
        if (activeWidgets != null && !activeWidgets.isEmpty()) {
            for (WidgetStatus status : activeWidgets) {
                status.afterQueryBuild(query);
            }
        }

        RequestCycle.get().setMetaData(QUERY, query);

        return query;
    }

    public void beforeQueryBuild(Query query) {
        // Override this method if the page contributes to the query build
    }

    public void onQueryBuild(Query query) {
        // Override this method if the page contributes to the query build
    }

    public void afterQueryBuild(Query query) {
        // Override this method if the page contributes to the query build
    }

    public void encodeParameters(PageParameters parameters, String keyPrefix, boolean global) {

        if (widgetStatuses != null && !global) {

            for (WidgetStatus status : widgetStatuses) {
                status.getConfig().setPageConfig(getConfig());
                status.encodeParameters(parameters, keyPrefix + "w" + status.getId());
            }
        }

    }

    public void decodeParameters(PageParameters parameters, String keyPrefix) {

        if (widgetStatuses != null) {
            for (WidgetStatus status : widgetStatuses) {
                status.decodeParameters(parameters, keyPrefix + "w" + status.getId());
            }
        }

    }
}
