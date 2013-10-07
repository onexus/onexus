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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.utils.panels.icons.Icons;
import org.onexus.website.api.widgets.IWidgetManager;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetModel;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.Collection;

public class VerticalWidgetBar extends Panel {
    private static final long serialVersionUID = 1L;

    @PaxWicketBean(name = "widgetManager")
    private IWidgetManager widgetManager;

    private RepeatingView boxesView;

    public VerticalWidgetBar(final String componentId, final Collection<WidgetConfig> widgets, IModel<BrowserPageStatus> pageModel) {
        super(componentId);
        setOutputMarkupId(true);

        // Create the containers
        WebMarkupContainer boxesshowContainer = new WebMarkupContainer("boxesshowContainer");
        add(boxesshowContainer);

        WebMarkupContainer boxesContainer = new WebMarkupContainer("boxesContainer");
        boxesshowContainer.add(boxesContainer);

        boxesView = new RepeatingView("boxes");
        boxesContainer.setMarkupId("leftboxes");
        boxesContainer.add(boxesView);

        // Create boxes
        if (widgets != null) {
            for (WidgetConfig widget : widgets) {
                WebMarkupContainer item = new WebMarkupContainer(boxesView.newChildId());
                boxesView.add(item);

                Link<String> boxLink = new Link<String>("cross") {
                    @Override
                    public void onClick() {
                    }
                };

                item.add(boxLink);
                boxLink.setVisible(false);
                boxLink.add(new Image("image", Icons.CROSS) {

                    @Override
                    protected boolean shouldAddAntiCacheParameter() {
                        return false;
                    }

                });

                item.add(widgetManager.create("widget", new WidgetModel(widget.getId(), pageModel)));
            }
        }

        showHideBoxes();
    }

    private void showHideBoxes() {
        boxesView.setVisible(true);
    }
}
