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
package org.onexus.website.api.widgets;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class WidgetStatus<C extends WidgetConfig> implements Serializable {

    @NotNull @Pattern(regexp = Resource.PATTERN_ID)
    private String id;

    private String base;

    private transient C config;

    private List<WidgetStatus> children = new ArrayList<WidgetStatus>();

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

    public String getButton() {
        return config.getButton();
    }

    public void setConfig(C config) {
        this.config = config;
    }

    public String getBase() {
        if (base == null) {
            base = getConfig().getBase();
        }
        return base;
    }

    public void setBase(String baseURI) {
        this.base = baseURI;
    }

    public WidgetStatus getChild(String id) {

        if (id == null) {
            return null;
        }

        for (WidgetStatus status : getChildren()) {
            if (id.equals(status.getId())) {
                return status;
            }
        }

        return null;
    }

    public void setWidgetStatus(WidgetStatus status) {
        WidgetStatus oldStatus = getChild(status.getId());
        children.add(status);
        children.remove(oldStatus);
    }

    public List<WidgetStatus> getChildren() {
        return children;
    }

    public List<WidgetStatus> getActiveChildren(ORI parentOri) {
        return getChildren();
    }

    public void setChildren(List<WidgetStatus> children) {
        this.children = children;
    }


    public void beforeQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
        // but call super at the end to let children widgets contribute
        for (WidgetStatus status : getActiveChildren(query.getOn())) {
            status.beforeQueryBuild(query);
        }
    }

    public void onQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
        // but call super at the end to let children widgets contribute
        for (WidgetStatus status : getActiveChildren(query.getOn())) {
            status.onQueryBuild(query);
        }
    }

    public void afterQueryBuild(Query query) {
        // Override this method if this widget contributes to the query
        // but call super at the end to let children widgets contribute
        for (WidgetStatus status : getActiveChildren(query.getOn())) {
            status.afterQueryBuild(query);
        }
    }

    public void encodeParameters(PageParameters parameters, String keyPrefix, boolean global) {

        if (children != null && !global) {

            for (WidgetStatus status : children) {
                status.getConfig().setParentConfig(getConfig());
                status.encodeParameters(parameters, keyPrefix + "w" + status.getId());
            }
        }

    }

    public void encodeParameters(PageParameters parameters, String keyPrefix) {
        encodeParameters(parameters, keyPrefix, false);
    }

    public void decodeParameters(PageParameters parameters, String keyPrefix) {

        if (children != null) {
            for (WidgetStatus status : children) {
                status.decodeParameters(parameters, keyPrefix + "w" + status.getId());
            }
        }

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
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WidgetStatus other = (WidgetStatus) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
