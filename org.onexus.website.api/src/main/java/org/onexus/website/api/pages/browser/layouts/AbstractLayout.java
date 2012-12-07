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
package org.onexus.website.api.pages.browser.layouts;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.*;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.pages.browser.ViewConfig;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.WidgetConfig;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class AbstractLayout extends Panel {

    public AbstractLayout(String panelId, IModel<BrowserPageStatus> statusModel) {
        super(panelId, statusModel);
    }

    public PageConfig getPageConfig() {
        BrowserPageStatus status = getPageStatus();
        return (status == null ? null : status.getConfig());
    }

    public BrowserPageStatus getPageStatus() {
        return (BrowserPageStatus) getDefaultModelObject();
    }

    public List<WidgetConfig> filterWidgets(String selectedWidgets) {
        List<WidgetConfig> widgets = ViewConfig.getSelectedWidgetConfigs(getPageConfig(), selectedWidgets);
		VisiblePredicate predicate = new VisiblePredicate(getPageStatus().getORI(), getPageStatus().getFilters());
		List<WidgetConfig> visibleWidgets = new ArrayList<WidgetConfig>();
		CollectionUtils.select(widgets, predicate, visibleWidgets);
		return visibleWidgets;
    }

    protected boolean isEmbed() {
        StringValue embed = getPage().getPageParameters().get("embed");
        return embed.toBoolean(false);
    }

}
