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
package org.onexus.website.api.pages.browser;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.website.api.utils.visible.IVisible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ResourceAlias("tab")
public class TabConfig implements Serializable, IVisible {

    private String id;
    private String title;
    private String help;
    private String visible;
    private String group;

    @ResourceImplicitList("view")
    private List<ViewConfig> views = new ArrayList<ViewConfig>();

    @ResourceImplicitList("map")
    private List<String> maps = new ArrayList<String>();

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

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public ViewConfig getView(String viewId) {

        if (viewId == null) {
            return null;
        }

        for (ViewConfig view : getViews()) {
            if (viewId.equals(view.getTitle())) {
                return view;
            }
        }

        return null;
    }

    public List<ViewConfig> getViews() {
        return views;
    }

    public void setViews(List<ViewConfig> views) {
        this.views = views;
    }

    public List<String> getMaps() {
        return maps;
    }

    public void setMaps(List<String> maps) {
        this.maps = maps;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
