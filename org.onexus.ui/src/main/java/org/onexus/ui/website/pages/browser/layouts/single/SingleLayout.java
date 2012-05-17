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
package org.onexus.ui.website.pages.browser.layouts.single;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.widgets.IWidgetManager;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetModel;

import javax.inject.Inject;

public class SingleLayout extends Panel {

    public static final String LAYOUT = "single";

    public static final String REGION_MAIN = "main";
    public static final CssResourceReference CSS = new CssResourceReference(SingleLayout.class, "SingleLayout.css");

    @Inject
    public IWidgetManager widgetManager;

    public SingleLayout(String panelId, ViewConfig viewConfig, IModel<BrowserPageStatus> statusModel) {
        super(panelId);

        if (viewConfig.getMain() == null) {
            add(new EmptyPanel("widget"));
        } else {

            // Get first widget
            String widgetId = viewConfig.getMain().split(",")[0];

            // Add widget
            add(widgetManager.create("widget", new WidgetModel(widgetId, statusModel)));
        }

    }
    
    protected CssResourceReference getCssResourceReference() {
        return CSS;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        
        CssResourceReference css = getCssResourceReference();
        if (css != null) {
            response.render(CssHeaderItem.forReference(CSS));
        }
    }

}
