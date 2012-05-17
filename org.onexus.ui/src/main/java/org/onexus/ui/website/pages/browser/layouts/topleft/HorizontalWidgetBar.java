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
package org.onexus.ui.website.pages.browser.layouts.topleft;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.IWidgetManager;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.WidgetModel;

import javax.inject.Inject;
import java.util.Collection;

public class HorizontalWidgetBar extends Panel {

    @Inject
    public IWidgetManager widgetManager;

    public HorizontalWidgetBar(String componentId, Collection<WidgetConfig> widgets, IModel<BrowserPageStatus> pageModel) {
        super(componentId);

        RepeatingView widgetsContainer = new RepeatingView("widgetsContainer");

        // Add all the widgets
        if (widgets != null) {
            for (WidgetConfig widget : widgets) {
                WebMarkupContainer item = new WebMarkupContainer(widgetsContainer.newChildId());
                widgetsContainer.add(item);

                item.add(widgetManager.create("widget", new WidgetModel(widget.getId(), pageModel)));
            }
        }

        add(widgetsContainer);
    }
}
