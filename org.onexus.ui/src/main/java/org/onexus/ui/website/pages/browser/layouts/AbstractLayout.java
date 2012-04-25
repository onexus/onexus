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
package org.onexus.ui.website.pages.browser.layouts;

import org.apache.wicket.markup.html.panel.Panel;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.List;

public class AbstractLayout extends Panel {

    public AbstractLayout(String panelId, IPageModel<BrowserPageStatus> statusModel) {
        super(panelId, statusModel);
    }

    public IPageModel<BrowserPageStatus> getPageModel() {
        return (IPageModel<BrowserPageStatus>) getDefaultModel();
    }

    public PageConfig getPageConfig() {
        return getPageModel().getConfig();
    }

    public BrowserPageStatus getPageStatus() {
        return getPageModel().getObject();
    }

    protected List<WidgetConfig> filterWidgets(String selectedWidgets) {

        List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();

        if (selectedWidgets != null) {
            for (String widget : selectedWidgets.split(",")) {
                WidgetConfig widgetConfig = getPageConfig().getWidget(widget.trim());
                if (widgetConfig!=null) {
                    widgets.add(widgetConfig);
                }
            }
        }

        return widgets;
    }
}
