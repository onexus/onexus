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

import org.apache.commons.lang3.StringUtils;
import org.onexus.core.ICollectionManager;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetStatus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserPageStatus extends PageStatus<BrowserPageConfig> {

    private String release;

    private String currentTabId;

    private String currentView;

    private Map<String, IFilter> filters = new HashMap<String, IFilter>();

    @Inject
    public ICollectionManager collectionManager;

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    @Override
    public List<WidgetStatus> getActiveWidgetStatuses() {

        BrowserPageConfig pageConfig = getConfig();
        TabConfig tabConfig = pageConfig.getTab(currentTabId);
        ViewConfig viewConfig = tabConfig.getView(currentView);

        return ViewConfig.getSelectedWidgetStatuses(
                this,
                viewConfig.getLeft(),
                viewConfig.getTop(),
                viewConfig.getTopRight(),
                viewConfig.getMain()
        );
    }

    public String getRelease() {

        if (release == null) {
            release = getConfig().getRelease();
        }

        return release;
    }

    public String getCurrentTabId() {
        return currentTabId;
    }

    public void setCurrentTabId(String currentTabId) {
        if (currentTabId != null && !currentTabId.equals(this.currentTabId)) {
            this.currentView = null;
        }
        this.currentTabId = currentTabId;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    public void setRelease(String releaseURI) {
        this.release = releaseURI;
    }

    public Map<String, IFilter> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, IFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void beforeQueryBuild(Query query) {

        if (!StringUtils.isEmpty(getRelease())) {
            query.setOn(ResourceUtils.concatURIs(query.getOn(), getRelease()));
        }

    }

    @Override
    public void afterQueryBuild(Query query) {

        if (filters != null) {
            for (IFilter fe : filters.values()) {
                if (getCollectionManager().isLinkable(query, fe.getFilteredCollection())) {
                    Filter filter = fe.buildFilter(query);
                    QueryUtils.and(query, filter);
                    fe.setEnable(true);
                } else {
                    fe.setEnable(false);
                }
            }
        }

    }

    @Override
    public void onQueryBuild(Query query) {

    }

    private ICollectionManager getCollectionManager() {

        if (collectionManager == null) {
            OnexusWebApplication app = OnexusWebApplication.get();
            if (app != null) {
                app.getInjector().inject(this);
            }
        }

        return collectionManager;
    }
}
