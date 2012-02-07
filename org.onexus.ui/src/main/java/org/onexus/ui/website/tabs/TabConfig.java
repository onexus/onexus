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

import org.onexus.ui.website.IWebsiteConfig;
import org.onexus.ui.website.utils.visible.IVisible;
import org.onexus.ui.website.viewers.EmptyViewerConfig;
import org.onexus.ui.website.viewers.ViewerConfig;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class TabConfig implements IWebsiteConfig, IVisible {

    private String id;
    private String title;
    private String visible;

    private List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();
    private List<ViewerConfig> viewers = new ArrayList<ViewerConfig>();

    public TabConfig() {
        super();
    }

    public TabConfig(String tabId, String title) {
        super();
        this.id = tabId;
        this.title = title;
        this.visible = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public List<WidgetConfig> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WidgetConfig> widgets) {
        this.widgets = widgets;
    }

    public List<ViewerConfig> getViewers() {
        return viewers;
    }

    @Override
    public abstract TabStatus getDefaultStatus();

    @Override
    public abstract TabStatus createEmptyStatus();

    public ViewerConfig getViewerConfig(String viewerId) {

        if (viewers != null) {
            for (ViewerConfig view : viewers) {
                if (view.getId().equals(viewerId)) {
                    return view;
                }
            }
        }
        return EmptyViewerConfig.get();
    }

    public void setViewers(List<ViewerConfig> views) {
        this.viewers = views;
    }

    protected static String getFirstViewerId(List<ViewerConfig> viewers) {
        if (viewers != null && !viewers.isEmpty()) {
            return viewers.get(0).getId();
        }

        return null;
    }

}
