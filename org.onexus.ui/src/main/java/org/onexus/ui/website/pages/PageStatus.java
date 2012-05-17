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
package org.onexus.ui.website.pages;

import org.onexus.core.query.Query;
import org.onexus.ui.website.widgets.WidgetStatus;

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

    public List<WidgetStatus> getActiveWidgetStatuses() {
        return getWidgetStatuses();
    }

    public void setWidgetStatuses(List<WidgetStatus> widgetStatuses) {
        this.widgetStatuses = widgetStatuses;
    }

    public Query buildQuery(String resourceUri) {

        Query query = new Query();

        // Website contributions
        query.setOn(resourceUri);

        // Page contributions
        onQueryBuild(query);

        // Widget contributions
        List<WidgetStatus> activeWidgets = getActiveWidgetStatuses();
        if (activeWidgets != null && !activeWidgets.isEmpty()) {
            for (WidgetStatus status : activeWidgets) {
                status.onQueryBuild(query);
            }
        }

        return query;
    }

    public void onQueryBuild(Query query) {
        // Override this method if the page contributes to the query build
    }
}
