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

import org.apache.commons.lang3.SerializationUtils;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.utils.authorization.IAuthorization;
import org.onexus.website.api.utils.visible.IVisible;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PageConfig implements IVisible, Serializable, IAuthorization {

    private String id;
    private String label;
    private String title;
    private String description;
    private String authorization;
    private String visible;

    private String css;

    private transient WebsiteConfig websiteConfig;

    public PageConfig() {
        super();
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public WebsiteConfig getWebsiteConfig() {
        return websiteConfig;
    }

    public void setWebsiteConfig(WebsiteConfig websiteConfig) {
        this.websiteConfig = websiteConfig;
    }

    public WidgetConfig getWidget(String id) {

        if (id != null) {
            for (WidgetConfig config : getWidgets()) {
                if (id.equals(config.getId())) {
                    return config;
                }
            }
        }
        return null;
    }

    public abstract List<WidgetConfig> getWidgets();

    public abstract PageStatus createEmptyStatus();

    public PageStatus newStatus() {

        PageStatus status = getDefaultStatus();

        if (status != null) {
            status = SerializationUtils.clone(status);
        } else {
            status = createEmptyStatus();
        }

        status.setId(getId());
        status.setConfig(this);

        // Add widget status
        List<WidgetConfig> widgets = getWidgets();
        List<WidgetStatus> statuses = new ArrayList<WidgetStatus>(widgets.size());
        for (WidgetConfig widgetConfig : getWidgets()) {
            statuses.add(widgetConfig.newStatus());
        }
        status.setWidgetStatuses(statuses);

        return status;
    }

    public abstract PageStatus getDefaultStatus();


}
