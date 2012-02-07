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
package org.onexus.ui.website.tabs;

import org.onexus.ui.website.IWebsiteStatus;
import org.onexus.ui.website.viewers.ViewerStatus;
import org.onexus.ui.website.widgets.WidgetStatus;

import java.util.HashSet;
import java.util.Set;

public abstract class TabStatus implements IWebsiteStatus {

    private String id;

    private String currentViewer;

    private Set<WidgetStatus> widgetStatus = new HashSet<WidgetStatus>();

    private Set<ViewerStatus> viewerStatus = new HashSet<ViewerStatus>();

    public TabStatus() {
        super();
    }

    public TabStatus(String tabId, String currentViewer) {
        super();

        this.id = tabId;
        this.currentViewer = currentViewer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentViewer() {
        return currentViewer;
    }

    public void setCurrentViewer(String currentViewer) {
        this.currentViewer = currentViewer;
    }

    public WidgetStatus getWidgetStatus(String id) {

        // TODO Cache the current widget status and avoid this loop
        for (WidgetStatus value : widgetStatus) {
            if (value.getId().equals(id)) {
                return value;
            }
        }

        return null;
    }

    public <T extends WidgetStatus> void setWidgetStatus(T value) {
        if (value != null) {
            widgetStatus.remove(value);
            widgetStatus.add(value);
        }
    }

    public ViewerStatus getViewerStatus(String id) {

        // TODO Cache the current viewer status and avoid this loop
        for (ViewerStatus value : viewerStatus) {
            if (value.getId().equals(id)) {
                return value;
            }
        }

        return null;

    }

    public ViewerStatus getCurrentViewerStatus() {
        return getViewerStatus(getCurrentViewer());
    }

    public <T extends ViewerStatus> void setViewerStatus(T value) {
        if (value != null) {
            viewerStatus.add(value);
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TabStatus other = (TabStatus) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}
