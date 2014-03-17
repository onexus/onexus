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


import org.apache.commons.lang3.SerializationUtils;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.utils.authorization.IAuthorization;
import org.onexus.website.api.utils.visible.IVisible;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WidgetConfig extends Resource implements Serializable, IAuthorization, IVisible {

    @Pattern(regexp = Resource.PATTERN_ID)
    private String id;

    private String label;

    private String title;

    private String description;

    private String authorization;

    private String visible;

    private String css;

    private String markup;

    private String button;

    private String width;

    private String base;

    private transient WidgetConfig parentConfig;

    private transient WebsiteConfig websiteConfig;

    public WidgetConfig() {
        super();
    }

    public WidgetConfig(String id) {
        super();
        this.id = id;

    }

    @Override
    public ORI getORI() {
        ORI ownOri = super.getORI();

        if (ownOri == null) {
            return getWebsiteConfig().getORI();
        }

        return ownOri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public WidgetConfig getParentConfig() {
        return parentConfig;
    }

    public WebsiteConfig getWebsiteConfig() {
        return websiteConfig;
    }

    public void setWebsiteConfig(WebsiteConfig websiteConfig) {
        this.websiteConfig = websiteConfig;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setParentConfig(WidgetConfig parentConfig) {
        this.parentConfig = parentConfig;
    }

    public WidgetConfig getChild(String id) {

        if (id != null) {
            for (WidgetConfig config : getChildren()) {
                if (id.equals(config.getId())) {
                    return config;
                }
            }
        }
        return null;
    }

    public List<WidgetConfig> getChildren() {
        return Collections.emptyList();
    }

    public abstract WidgetStatus getDefaultStatus();

    public abstract WidgetStatus createEmptyStatus();

    public WidgetStatus newStatus() {

        WidgetStatus status = getDefaultStatus();

        if (status != null) {
            status = SerializationUtils.clone(status);
        } else {
            status = createEmptyStatus();
        }

        status.setId(getId());
        status.setConfig(this);

        // Add children widget status
        List<WidgetConfig> widgets = getChildren();
        List<WidgetStatus> statuses = new ArrayList<WidgetStatus>(widgets.size());
        for (WidgetConfig widgetConfig : getChildren()) {
            widgetConfig.setParentConfig(this);
            widgetConfig.setWebsiteConfig(getWebsiteConfig());
            statuses.add(widgetConfig.newStatus());
        }
        status.setChildren(statuses);

        return status;
    }

}
