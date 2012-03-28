/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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

import org.onexus.ui.website.widgets.WidgetStatus;

import java.io.Serializable;
import java.util.*;

public abstract class PageStatus implements Serializable {

    private String id;
    
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

    public void setWidgetStatuses(List<WidgetStatus> widgetStatuses) {
        this.widgetStatuses = widgetStatuses;
    }
}
