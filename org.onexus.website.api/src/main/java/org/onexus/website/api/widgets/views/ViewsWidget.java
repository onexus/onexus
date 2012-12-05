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
package org.onexus.website.api.widgets.views;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.website.api.events.EventViewChange;
import org.onexus.website.api.pages.browser.BrowserPage;
import org.onexus.website.api.pages.browser.BrowserPageConfig;
import org.onexus.website.api.pages.browser.BrowserPageStatus;
import org.onexus.website.api.pages.browser.ViewConfig;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.Widget;

import java.util.ArrayList;
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

            List<ViewConfig> filteredViews = new ArrayList<ViewConfig>();
            VisiblePredicate predicate = new VisiblePredicate(getPageBaseOri().getParent(), getPageStatus().getFilters());
            CollectionUtils.select(views, predicate, filteredViews);

            if (filteredViews != null && !filteredViews.isEmpty()) {
                for (ViewConfig view : filteredViews) {

                    WebMarkupContainer item = new WebMarkupContainer(viewsContainer.newChildId());
                    viewsContainer.add(item);

                    AjaxLink<String> link = new IndicatingAjaxLink<String>("link", Model.of(view.getTitle())) {
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
