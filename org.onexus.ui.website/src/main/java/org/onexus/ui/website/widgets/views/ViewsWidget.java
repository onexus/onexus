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
package org.onexus.ui.website.widgets.views;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.widgets.Widget;

import java.util.List;

public class ViewsWidget extends Widget<ViewsWidgetConfig, ViewsWidgetStatus> {

    public ViewsWidget(String componentId, IModel statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventViewChange.class);

    }

    @Override
    protected void onBeforeRender() {

        RepeatingView viewsContainer = new RepeatingView("views");
        add(viewsContainer);

        BrowserPageStatus browserStatus = getPageStatus();

        setVisible(false);
        if (browserStatus != null) {
            BrowserPageConfig browserConfig = browserStatus.getConfig();
            List<ViewConfig> views = browserConfig.getTab(browserStatus.getCurrentTabId()).getViews();
            if (views != null && !views.isEmpty()) {
                for (ViewConfig view : views) {

                    WebMarkupContainer item = new WebMarkupContainer(viewsContainer.newChildId());
                    viewsContainer.add(item);

                    AjaxLink<String> link = new AjaxLink<String>("link", Model.of(view.getTitle())) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            BrowserPageStatus browserStatus = getPageStatus();
                            browserStatus.setCurrentView(getModelObject());

                            send(getPage(), Broadcast.BREADTH, EventViewChange.EVENT);
                        }
                    };
                    link.add(new Label("label", view.getTitle()));
                    item.add(link);

                    if (view.getTitle().equals(browserStatus.getCurrentView())) {
                        item.add(new AttributeModifier("class", "active"));
                    }
                }
                setVisible(true);
            }
        }

        super.onBeforeRender();
    }

    private BrowserPageStatus getPageStatus() {
        return findParent(BrowserPage.class).getStatus();
    };

    private BrowserPageConfig getPageConfig() {
        return (BrowserPageConfig) getPageStatus().getConfig();
    };

}
