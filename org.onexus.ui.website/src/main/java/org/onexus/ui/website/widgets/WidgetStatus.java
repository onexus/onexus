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
package org.onexus.ui.website.widgets;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.collection.api.query.Query;

import java.io.Serializable;

public abstract class WidgetStatus<C extends WidgetConfig> implements Serializable {

    private String id;

    private transient C config;

    public WidgetStatus() {
        super();
    }

    public WidgetStatus(String widgetId) {
        super();
        this.id = widgetId;
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

    public void beforeQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
    }

    public void onQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
    }

    public void afterQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
    }

    public void encodeParameters(PageParameters parameters, String keyPrefix) {

    }

    public void decodeParameters(PageParameters parameters, String keyPrefix) {

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WidgetStatus other = (WidgetStatus) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
