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
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.utils.authorization.IAuthorization;
import org.onexus.website.api.utils.visible.IVisible;

import java.io.Serializable;

public abstract class WidgetConfig implements Serializable, IAuthorization, IVisible {

    private String id;

    private String button;

    private String width;

    private String title;

	private String authorization;

	private String visible;

    private transient PageConfig pageConfig;

    public WidgetConfig() {
        super();
    }

    public WidgetConfig(String id) {
        super();
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

	public PageConfig getPageConfig() {
        return pageConfig;
    }

    public void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
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

        return status;
    }

}
