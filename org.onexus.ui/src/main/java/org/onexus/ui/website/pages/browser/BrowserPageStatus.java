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
import org.onexus.core.query.EqualId;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.utils.FixedEntity;
import org.onexus.ui.website.widgets.WidgetStatus;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrowserPageStatus extends PageStatus<BrowserPageConfig> {

    private String release;

    private String currentTabId;

    private String currentView;

    private Set<FixedEntity> fixedEntities = new HashSet<FixedEntity>();

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

    public Set<FixedEntity> getFixedEntities() {
        return fixedEntities;
    }

    public void setFixedEntities(Set<FixedEntity> fixedEntities) {
        this.fixedEntities = fixedEntities;
    }

    @Override
    public void beforeQueryBuild(Query query) {

        if (!StringUtils.isEmpty(getRelease())) {
            query.setOn(ResourceUtils.concatURIs(query.getOn(), getRelease()));
        }

    }

    @Override
    public void afterQueryBuild(Query query) {

        if (fixedEntities != null) {
            for (FixedEntity fe : fixedEntities) {
                if (getCollectionManager().isLinkable(query, fe.getCollectionURI())) {
                    String collectionAlias = QueryUtils.newCollectionAlias(query, fe.getCollectionURI());
                    QueryUtils.and(query, new EqualId(collectionAlias, fe.getEntityId()));
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
