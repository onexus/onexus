/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.onexus.core.resources.Release;
import org.onexus.core.utils.ResourceTools;
import org.onexus.ui.OnexusWebSession;
import org.onexus.ui.website.events.EventFixEntity;
import org.onexus.ui.website.events.EventTabSelected;
import org.onexus.ui.website.events.EventUnfixEntity;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.Page;
import org.onexus.ui.website.tabs.ITabManager;
import org.onexus.ui.website.tabs.TabConfig;
import org.onexus.ui.website.tabs.TabStatus;
import org.onexus.ui.website.utils.visible.FixedEntitiesVisiblePredicate;
import org.onexus.ui.website.widgets.bookmark.StatusEncoder;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BrowserPage extends Page<BrowserPageConfig, BrowserPageStatus> {

    public final static CssResourceReference CSS = new CssResourceReference(BrowserPage.class, "BrowserPage.css");

    @Inject
    public ITabManager tabManager;


    public BrowserPage(String componentId, BrowserPageConfig config, IModel<BrowserPageStatus> statusModel) {
        super(componentId, config, statusModel);
        onEventFireUpdate(EventTabSelected.class);

        checkRelease();

        add(new FixedEntities("position", config, statusModel));

        onEventFireUpdate(EventFixEntity.class, EventUnfixEntity.class, EventViewChange.class);
    }


    protected boolean isCurrentTab(String tabId) {
        String currentTabId = getStatus().getCurrentTabId();
        return tabId.equals(currentTabId);
    }

    protected TabConfig getCurrentTab() {
        String currentTabId = getStatus().getCurrentTabId();
        return getConfig().getTab(currentTabId);
    }

    private void checkRelease() {

        BrowserPageStatus status = getStatus();
        String releaseUri = status.getReleaseURI();

        if (releaseUri == null) {

            String parentURI = ResourceTools.getParentURI(getWebsiteConfig().getURI());
            List<Release> releases = OnexusWebSession.get().getResourceManager().loadChildren(Release.class, parentURI);

            if (releases != null && !releases.isEmpty()) {
                status.setReleaseURI(releases.get(0).getURI());
            }

        }

    }

    @Override
    protected void onBeforeRender() {

        checkRelease();

        VisibleTabs visibleTabs = new VisibleTabs();

        // Tabs can change when we fix/unfix entities
        addOrReplace(new ListView<TabConfig>("tabs", visibleTabs) {

            @Override
            protected void populateItem(ListItem<TabConfig> item) {

                TabConfig tab = item.getModelObject();

                BrowserPageLink<TabStatus> link = new BrowserPageLink<TabStatus>("link", Model.of(tab
                        .getDefaultStatus())) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        TabStatus defaultStatus = null;
                        try {
                            defaultStatus = getModelObject();
                            StatusEncoder encoder = new StatusEncoder(getClass().getClassLoader());
                            defaultStatus = encoder.decodeStatus(encoder.encodeStatus(defaultStatus));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        getStatus().setCurrentTabId(defaultStatus.getId());
                        getStatus().setTabStatus(defaultStatus);

                        sendEvent(EventTabSelected.EVENT);

                    }

                };

                String tabTitle = (tab.getTitle() == null ? tab.getId() : tab.getTitle());

                link.add(new Label("label", tabTitle));
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

        // Add the tab panel
        TabConfig tabConfig = getCurrentTab();
        addOrReplace(tabManager.create("content", tabConfig, new TabModel(tabConfig, getModelStatus())));

        super.onBeforeRender();
    }

    private class VisibleTabs extends AbstractReadOnlyModel<List<TabConfig>> {

        @Override
        public List<TabConfig> getObject() {

            // All the defined tabs in the configuration
            List<TabConfig> allTabs = getConfig().getTabs();

            // A predicate that filters the visible tabs
            Predicate filter = new FixedEntitiesVisiblePredicate(getStatus().getReleaseURI(), getStatus()
                    .getFixedEntities());

            // Return a new collection with only the visible tabs
            List<TabConfig> tabs = new ArrayList<TabConfig>();
            CollectionUtils.select(allTabs, filter, tabs);
            return tabs;
        }

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(CSS);
    }

}
