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
package org.onexus.ui.website.pages.browser;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.utils.visible.IVisible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("tab")
public class TabConfig implements Serializable, IVisible {

    private String id;
    private String title;
    private String visible;
    private String group;

    @XStreamImplicit(itemFieldName="view")
    private List<ViewConfig> views = new ArrayList<ViewConfig>();

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
    
    public ViewConfig getView(String viewId) {

        if (viewId == null) {
            return null;
        }
        
        for (ViewConfig view : getViews()) {
            if (viewId.equals(view.getTitle())) {
                return  view;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
