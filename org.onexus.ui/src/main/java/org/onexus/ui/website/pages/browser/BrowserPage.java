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
package org.onexus.ui.website.pages.browser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.IResourceManager;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventTabSelected;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.Page;
import org.onexus.ui.website.pages.browser.layouts.leftmain.LeftMainLayout;
import org.onexus.ui.website.pages.browser.layouts.single.SingleLayout;
import org.onexus.ui.website.pages.browser.layouts.topleft.TopleftLayout;
import org.onexus.ui.website.pages.browser.layouts.topmain.TopmainLayout;
import org.onexus.ui.website.utils.visible.VisiblePredicate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class BrowserPage extends Page<BrowserPageConfig, BrowserPageStatus> {

    @Inject
    public IResourceManager resourceManager;

    public BrowserPage(String componentId, IModel<BrowserPageStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventTabSelected.class);

        add(new FiltersPanel("position", statusModel));

        onEventFireUpdate(EventFixEntity.class, EventUnfixEntity.class, EventViewChange.class);
    }


    protected boolean isCurrentTab(String tabId) {
        return tabId.equals(getStatus().getCurrentTabId());

    }

    protected TabConfig getCurrentTab() {
        String currentTabId = getStatus().getCurrentTabId();
        return getConfig().getTab(currentTabId);
    }

    @Override
    protected void onBeforeRender() {

        VisibleTabs visibleTabs = new VisibleTabs();

        // Tabs can change when we fix/unfix entities
        addOrReplace(new ListView<TabConfig>("tabs", visibleTabs) {

            @Override
            protected void populateItem(ListItem<TabConfig> item) {

                TabConfig tab = item.getModelObject();

                BrowserPageLink<String> link = new BrowserPageLink<String>("link", Model.of(tab.getId())) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getStatus().setCurrentTabId(getModelObject());
                        sendEvent(EventTabSelected.EVENT);
                    }

                };

                link.add(new Label("label", tab.getTitle()));
                item.add(link);

                if (isCurrentTab(tab.getId())) {
                    item.add(new AttributeModifier("class", new Model<String>("selected")));
                }

            }

        });

        // Check if the current selected tab is visible.
            String currentTabId = getStatus().getCurrentTabId();

        // Set a current tab if there is no one.
        if (currentTabId == null) {

            List<TabConfig> tabs = getConfig().getTabs();
            if (tabs != null && !tabs.isEmpty()) {
                currentTabId = tabs.get(0).getId();
                getStatus().setCurrentTabId(currentTabId);
            }
        }

        List<TabConfig> tabs = visibleTabs.getObject();
        boolean hiddenTab = true;
        for (TabConfig tab : tabs) {
            if (tab.getId().equals(currentTabId)) {
                hiddenTab = false;
            }
        }

        if (hiddenTab && !tabs.isEmpty()) {
            TabConfig firstTab = tabs.get(0);
            getStatus().setCurrentTabId(firstTab.getId());
        }


        List<String> views = new ArrayList<String>();
        for (ViewConfig view : getCurrentTab().getViews()) {
            views.add(view.getTitle());
        }

        WebMarkupContainer viewSelector = new WebMarkupContainer("viewselector");

        if (getStatus().getCurrentView() == null && views.size() > 0) {
            getStatus().setCurrentView(views.get(0));
        }

        DropDownChoice<String> selector = new DropDownChoice<String>("select", new PropertyModel<String>(getModel(), "currentView"), views);
        selector.setNullValid(false);
        viewSelector.add(selector);

        selector.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                sendEvent(EventViewChange.EVENT);
            }
        });


        addOrReplace(viewSelector);
        viewSelector.setVisible(views.size() > 1);

        ViewConfig viewConfig = null;
        for (ViewConfig view : getCurrentTab().getViews()) {
            if (view.getTitle().equals(getStatus().getCurrentView())) {
                viewConfig = view;
                break;
            }
        }

        if (viewConfig.getLeft() != null && viewConfig.getTop() != null) {
            addOrReplace(new TopleftLayout("content", viewConfig, getModel()));
        } else if (viewConfig.getLeft() != null) {
            addOrReplace(new LeftMainLayout("content", viewConfig, getModel()));
        } else if (viewConfig.getTop() != null || viewConfig.getTopRight() != null) {
            addOrReplace(new TopmainLayout("content", viewConfig, getModel()));
        } else {
            addOrReplace(new SingleLayout("content", viewConfig, getModel()));
        }

        super.onBeforeRender();
    }

    private class VisibleTabs extends AbstractReadOnlyModel<List<TabConfig>> {

        @Override
        public List<TabConfig> getObject() {

            List<TabConfig> allTabs = getConfig().getTabs();

            // A predicate that filters the visible views
            Predicate filter = new VisiblePredicate(getStatus().getRelease(), getStatus().getFilters().values());

            // Return a new collection with only the visible tabs
            List<TabConfig> tabs = new ArrayList<TabConfig>();
            CollectionUtils.select(allTabs, filter, tabs);
            return tabs;
        }

    }

}
